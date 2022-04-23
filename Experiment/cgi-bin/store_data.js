#!/bin/env /bin/node

const crypto = require('crypto');
const { request } = require('http');

let postData = "";
let responseBuffer = "";

process.stdin.on('data', function(data) { postData += data; });
process.stdin.on('end', () => main(postData));

function print(data) {
    responseBuffer += data;
}

function println(data) {
    responseBuffer += data;
    console.log(responseBuffer.length.toString(16) + "\r");
    console.log(responseBuffer + "\r");
    responseBuffer = "";
}

function main(postData) {
    console.log("Access-Control-Allow-Origin:*");
    console.log("Access-Control-Allow-Headers:*");
    console.log("Content-Type: text/plain");
    console.log("Transfer-Encoding: chunked");
    console.log();

    const userIp = process.env['REMOTE_ADDR'];
    const cookies = process.env['HTTP_COOKIE'];
    const userHash = crypto.createHash('md5').update(cookies).digest("hex");
    const deviceDetails = {
        ip: process.env['REMOTE_ADDR'],
        userAgent: process.env['HTTP_USER_AGENT'],
        userHash: userHash,
        chromium: {
            agent: process.env['HTTP_SEC_CH_UA'],
            mobile: process.env['HTTP_SEC_UA_MOBILE'],
            platform: process.env['HTTP_SEC_UA_PLATFORM']
        }
    };
    const requestDetails = {
        time: (new Date()).toISOString(),
        query: process.env['QUERY_STRING'],
        body: responseBuffer
    }
    println(`deviceDetails: ${JSON.stringify(deviceDetails)}\n`)
    println(`requestDetails: ${JSON.stringify(requestDetails)}\n`)

    // Needed to terminate the chunked stream
    println("");
}