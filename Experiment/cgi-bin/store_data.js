#!/bin/env /bin/node

let postData = "";

console.log("Access-Control-Allow-Origin:*");
console.log("Access-Control-Allow-Headers:*");
console.log("Content-Type: text/plain");
console.log("\n\n");
console.log("Hello world!");
console.log("This is node.js!");

process.stdin.on('data', function(data) { postData += data; });

process.argv.forEach((val, index) => {
    console.log(`${index}: ${val}`)
})

console.log(process.env)

process.env.forEach((val) => {
    console.log(`${val}`)
})

console.log(`Post data: ${postData}`);

console.log("\r\n");