#!/bin/env /bin/node

let postData = "";
let response = "";

process.stdin.on('data', function(data) { postData += data; });

function print(data) {
    response += data;
}

function println(data) {
    response += data + "\n";
}

function sendResponse() {
    console.log("Access-Control-Allow-Origin:*");
    console.log("Access-Control-Allow-Headers:*");
    console.log("Content-Type: text/plain");
    console.log("Transfer-Encoding: chunked");
    console.log("\n");
    console.log(response.length + "\r\n");
    console.log(response + "\r\n");
}

println("Hello world!");
println("This is node.js!");

process.argv.forEach((val, index) => {
    println(`${index}: ${val}`)
})

println(process.env)

process.env.forEach((val) => {
    println(`${val}`)
})

println(`Post data: ${postData}`);


sendResponse();