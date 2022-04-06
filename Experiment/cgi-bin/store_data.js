#!/bin/env /bin/node

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

    println("Hello world!\n");
    println("This is node.js!");

    process.argv.forEach((val, index) => {
        println(`${index}: ${val}`)
    })

    for (const v in process.env) {
        println(`${v} ${process.env[v]}`)
    }

    println(`Post data: ${postData}`);

    // Needed to terminate the chunked stream
    println("");
}