const click = new Audio("click.wav");
const debugMode = !location.host;

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

var promptPromise;
var promptPromiseResolve;
var promptExpectedValue;
var promptExpectedValueFull;
var promptEntryValue;
var promptHintsEnabled;
var trials = [];
const okText = preparePrompt("OK");
const promptCommon = "Please type the words shown in this box and don't worry if you make a mistake, just keep typing.  Type " + okText + " to continue.";
const trialVariables = [{
        instructions: "In this trial you will see a standard keyboard. " + promptCommon,
        layout: layouts.qwerty,
        showHints: false,
        phrases: []
    },
    {
        instructions: "In this trial you will see an experimental keyboard. " + promptCommon,
        layout: layouts.metropolis,
        showHints: false,
        phrases: []
    },
    {
        instructions: "In this trial you will see a standard keyboard and <span class='hint'>typing hints</span>. " + promptCommon,
        layout: layouts.qwerty,
        showHints: true,
        phrases: []
    },
    {
        instructions: "In this trial you will see the experimental keyboard and <span class='hint'>typing hints</span>. " + promptCommon,
        layout: layouts.metropolis,
        showHints: true,
        phrases: []
    },
];

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
    // if (!source) {
    //     return "";
    // }
    let current = markov;
    for (let i = 0; i < source.length; i++) {
        current = current[source.substring(0, i + 1)];
        if (!current) {
            console.log(`${source} has no recommendations`);
            current = {};
        }
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
    for (let i = 0; i < promptExpectedValueFull.length - promptExpectedValue.length; i++) {
        document.querySelector(`#prompt > span[data-idx='${i}']`).classList.add("done");
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

function preparePrompt(str) {
    let out = "";
    for (let i = 0; i < str.length; i++) {
        out += `<span data-idx='${i}'>${str[i]}</span>`;
    }
    return out;
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
        key.addEventListener("pointerdown", e => {
            if (!debugMode && e.type === 'mouse') {
                return false;
            }
            keyPressed(key.getAttribute("data-key"));
        }, { passive: true });
    }
}

function initKeyboard(layout) {
    document.getElementById("keyboard").innerHTML = buildKeyboard(layout);
    setupKeys();
}

// Seed is returned by server after initial form submit
// This is so that we can follow a latin-squares assignment
async function startTrials(seed) {
    var phraseIndexes = [...Array(phrases.length).keys()]
        /* Randomize array in-place using Durstenfeld shuffle algorithm */
        // via https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
    for (var i = phraseIndexes.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = phraseIndexes[i];
        phraseIndexes[i] = phraseIndexes[j];
        phraseIndexes[j] = temp;
    }
    // TODO: Implement actual trial ordering from seed
    var trialOrder = [3, 0, 1, 2, 3];
    for (let i = 0; i < 4; i++) {
        trials[i] = trialVariables[trialOrder[i]];
        if (debugMode) {
            trials[i].phrases = ["aaa", "bbb", "ccc", "ddd", "eee"];
        } else {
            trials[i].phrases = phraseIndexes.slice(i * 5, (i + 1) * 5).map(i => phrases[i]);
        }
    }
    await performTrial(0);
}

function resetEntry() {
    document.getElementById("entry").innerHTML = "&gt;&gt; ";
    document.getElementById("prompt").innerHTML = " ";
    promptEntryValue = "";
}

async function waitForEntry(prompt) {
    promptPromise = new Promise((resolve, reject) => {
        promptPromiseResolve = resolve;
    });
    promptExpectedValue = prompt;
    promptExpectedValueFull = prompt;
    await promptPromise;
}

async function performTrial(number) {
    document.getElementById("greeting").style.display = "none";
    document.getElementById("prompt").style.display = "block";
    const trial = trials[number];
    resetEntry();
    document.getElementById("prompt").innerHTML = trial.instructions;
    promptHintsEnabled = trial.showHints;
    initKeyboard(trial.layout);
    adjustHints("ok");
    await waitForEntry("ok");
    for (let currentPhrase = 0; currentPhrase < trial.phrases.length; currentPhrase++) {
        resetEntry();
        document.getElementById("prompt").innerHTML = preparePrompt(trial.phrases[currentPhrase]);
        await waitForEntry(trial.phrases[currentPhrase]);
    }
    if (number < 3) {
        await performTrial(number + 1);
    } else {
        showClosing();
    }
}

function showGreeting() {
    document.getElementById("greeting").style.display = "block";
    document.getElementById("prompt").style.display = "none";
}

function showClosing() {
    document.getElementById("closing").style.display = "block";
    document.getElementById("prompt").style.display = "none";
    document.getElementById("keyboard").style.display = "none";
    document.getElementById("entry").style.display = "none";
}

async function finishGreeting() {
    const formData = new FormData(document.getElementById("greeting").querySelector("form"));
    const jsonData = Object.fromEntries(formData.entries());

    if (!debugMode) {
        const text = await storeResponse(jsonData);
    } else {
        text = "0123";
    }
    await startTrials(text);
}

async function finishClosing() {
    const formData = new FormData(document.getElementById("closing").querySelector("form"));
    const jsonData = Object.fromEntries(formData.entries());

    if (!debugMode) {
        await storeResponse(jsonData);
    }
    document.getElementById("closing").innerHTML = `
        <h1>Thank you!</h1>
        Your responses have been submitted.  You may close this webpage now.
    `;
}

async function init() {
    document.getElementById("greetingSubmit").onclick = finishGreeting;
    document.getElementById("closingSubmit").onclick = finishClosing;
    showGreeting();
}

document.addEventListener('DOMContentLoaded', init);