#!/bin/env /bin/node

console.log("Access-Control-Allow-Origin:*");
console.log();
console.log("Hello world!");
console.log("This is node.js!");

process.argv.forEach((val, index) => {
    console.log(`${index}: ${val}`)
})

console.log(process.env)

process.env.forEach((val) => {
    console.log(`${val}`)
})