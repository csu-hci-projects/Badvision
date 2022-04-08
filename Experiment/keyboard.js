const click = new Audio("click.wav");
const debugMode = true;

var promptPromise;
var promptPromiseResolve;
var promptExpectedValue;
var promptEntryValue;
var promptHintsEnabled;

// pipe is treated as a spacer (not a visible key)
const layouts = {
    qwerty: {
        style: "rectangular",
        rows: [
            "qwertyuiop",
            "asdfghjkl",
            "|zxcvbnm,.",
            "||| "
        ],
    },
    metropolis: {
        style: "hexagonal",
        rows: [
            "|bdlf",
            "caermq",
            "jhn oyz",
            "gitsuv",
            "|kpwx,."
        ]
    },
    dvorak: {
        style: "rectangular",
        rows: [
            "'.,pyfgcrl",
            "aoeuidhtns",
            "|;qjkxbmwvz",
            "||| "
        ]
    }
}

// Not sure if the server is 100% reliable so let's add a retry loop to be safe!
// From: https://dev.to/ycmjason/javascript-fetch-retry-upon-failure-3p6g
const fetch_retry = (url, options, n) => fetch(url, options).catch(
    function(error) {
        if (n === 1) throw error;
        return fetch_retry(url, options, n - 1);
    }
);

function storeResponse(data) {
    return fetch_retry("//www.cs.colostate.edu/~bdvision/cgi-bin/store_data.js", {
            method: "post",
            headers: {
                // "Content-Type": "application/json"
            },

            body: JSON.stringify(data)
        }, 3)
        .then((response) => response.text());
}

function adjustHints(letters) {
    for (const key of document.getElementsByClassName("key")) {
        const letter = key.getAttribute("data-key");
        if (letters.indexOf(letter) >= 0) {
            key.classList.add("hint");
        } else {
            key.classList.remove("hint");
        }
    }
}

function findHints(source) {
    if (!source) {
        return "";
    }
    let current = markov;
    for (let i = 0; i < source.length; i++) {
        current = current[source.substring(0, i + 1)];
    }
    let hints = "";
    for (let k of Object.keys(current)) {
        if (k == "value") {
            continue;
        }
        hints += k.substring(k.length - 1);
        if (hints.length == 4) break;
    }
    console.log(`${source} hints: ${hints}`);
    return hints;
}

function keyPressed(key) {
    document.getElementById("entry").innerHTML += key;
    promptEntryValue += key;
    click.play();
    if (navigator && navigator.vibrate) {
        navigator.vibrate(50);
    }
    if (!promptExpectedValue) {
        return;
    }
    // Scan ahead the next couple of characters in case the user skipped over one
    // like a typo, ommission should also be ignored.
    for (let i = 0; i < Math.min(promptExpectedValue.length, 2); i++) {
        const nextChar = promptExpectedValue.toLowerCase()[i];
        if (nextChar == key.toLowerCase()) {
            promptExpectedValue = promptExpectedValue.substring(i + 1);
            if (promptExpectedValue.length === 0) {
                promptPromiseResolve(promptEntryValue);
                adjustHints("");
                return;
            }
            break;
        }
    }
    if (promptHintsEnabled) {
        let startIndex = promptEntryValue.length;
        for (let i = promptEntryValue.length - 1; i > promptEntryValue.length - 3; i--) {
            const c = promptEntryValue.toLowerCase()[i];
            if (c >= 'a' && c <= 'z') {
                startIndex = i;
            } else {
                break;
            }
        }
        const hintSource = promptEntryValue.substring(startIndex);
        adjustHints(promptExpectedValue[0] + findHints(hintSource));

    } else {
        adjustHints("");
    }
}

function buildKeyboard(layout) {
    let out = "<div class='keyboard " + layout.style + "'>";
    layout.rows.forEach(row => {
        out += "<div class='row'>";
        for (const key of row) {
            const className = key === '|' ? 'spacer' : 'key';
            const keyVal = key === "'" ? '&apos;' : key;
            const keyToken = key === ' ' ? "Spc" : key;
            out += `<div class='${className}' data-key='${keyVal}'>${keyToken}</div>`;
        }
        out += "</div>";
    });
    out += "</div>";
    return out;
}

function setupKeys() {
    // Disable selection and context menu
    document.oncontextmenu = () => false;
    document.onselectstart = () => false;

    // Register key handler for touch events
    // Note this does not register mouse clicks!
    for (const key of document.getElementsByClassName("key")) {
        key.addEventListener("touchstart", e => keyPressed(key.getAttribute("data-key")), { passive: true });
        if (debugMode) {
            key.addEventListener("mousedown", e => keyPressed(key.getAttribute("data-key")), { passive: true });
        }
    }
}

function initKeyboard(layout) {
    document.getElementById("keyboard").innerHTML = buildKeyboard(layout);
    setupKeys();
}

function showGreeting() {
    document.getElementById("greeting").style.display = "block";
}

function getTrialVariables(number) {
    return {
        instructions: "In this trial you will see the experimental keyboard and hints.  Please type the words shown in this box and don't worry if you make a mistake, just keep typing.  Type OK to continue.",
        layout: layouts.metropolis,
        showHints: true
    };
}

function resetEntry() {
    document.getElementById("entry").innerHTML = "&gt;&gt; ";
    document.getElementById("prompt").innerHTML = " ";
}

function waitForEntry(prompt) {
    promptPromise = new Promise((resolve, reject) => {
        promptPromiseResolve = resolve;
    });
    promptExpectedValue = prompt;
    return promptPromise;
}

function performTrial(number) {
    document.getElementById("greeting").style.display = "none";
    const trial = getTrialVariables(number);
    document.getElementById("prompt").innerHTML = trial.instructions;
    promptHintsEnabled = trial.showHints;
    initKeyboard(trial.layout);
    adjustHints("ok");
    waitForEntry("ok").then(() => {
        resetEntry();
    });
}

function finishGreeting() {
    const formData = new FormData(document.getElementById("greeting").querySelector("form"));
    const jsonData = Object.fromEntries(formData.entries());

    storeResponse(jsonData).then(() => performTrial(0));
}

function init() {
    //showGreeting();
    performTrial(0);
}

document.addEventListener('DOMContentLoaded', init);