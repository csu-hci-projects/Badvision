#!/bin/env /bin/node

const crypto = require('crypto');
const fs = require('fs');

// Note: This is currently hard-coded to a specific user folder
// The files appear to be written by the user hosting the script
const targetDir = "/s/chopin/b/grad/bdvision/badvisionKeyboard";

let postData = "";
let responseBuffer = "";
const balancedLatinSquares = [
    "0132",
    "1203",
    "2310",
    "3021"
];

// Begin: Request input process
process.stdin.on('data', function(data) { postData += data; });
process.stdin.on('end', () => main(postData));
// End: Request input process

function print(data) {
    responseBuffer += data;
}

function println(data) {
    responseBuffer += data;
    console.log(responseBuffer.length.toString(16) + "\r");
    console.log(responseBuffer + "\r");
    responseBuffer = "";
}

function getTrialOrder() {
    const files = fs.readdirSync(targetDir);
    const idx = files.length % balancedLatinSquares.length;
    return balancedLatinSquares[idx];
}

async function main(postData) {
    console.log("Access-Control-Allow-Origin:*");
    console.log("Access-Control-Allow-Headers:*");
    console.log("Content-Type: text/plain");
    console.log("Transfer-Encoding: chunked");
    console.log();

    const userIp = process.env['REMOTE_ADDR'];
    const cookies = process.env['HTTP_COOKIE'] || "NONE";
    const userHash = crypto.createHash('md5').update(cookies).digest("hex");
    const deviceDetails = {
        ip: userIp,
        userAgent: process.env['HTTP_USER_AGENT'] || "UNKNOWN",
        userHash: userHash,
        clientHints: {
            agent: process.env['HTTP_SEC_CH_UA'] || "NONE",
            mobile: process.env['HTTP_SEC_CH_UA_MOBILE'] || "NONE",
            platform: process.env['HTTP_SEC_CH_UA_PLATFORM'] || "NONE"
        }
    };

    let inputData = {}
    if (inputData) {
        try {
            inputData = JSON.parse(postData)
        } catch (err) {
            inputData = postData
                // Do nothing, just don't parse the data
        }
    }

    const requestDetails = {
        time: (new Date()).toISOString(),
        query: process.env['QUERY_STRING'],
        body: inputData
    }

    if (inputData && inputData.device) {
        const order = getTrialOrder();
        requestDetails.trialOrder = order;
        println(order);
    }

    const dataLog = {
        device: deviceDetails,
        request: requestDetails
    }
    fs.appendFileSync(`${targetDir}/${userIp}_${userHash}.json`, `${JSON.stringify(dataLog)},\n`);

    // Needed to terminate the chunked stream
    println("");
}