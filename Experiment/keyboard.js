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
            "|kpex,."
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

function storeResponse(data) {
    fetch("//www.cs.colostate.edu/~bdvision/cgi-bin/store_data.js", {
            method: "post",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
        .then((response) => response.json())
        .then(console.log)
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

function keyPressed(key) {
    document.getElementById("entry").innerHTML += key;
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
    }
}

function init() {
    const layout = layouts.metropolis;
    document.getElementById("keyboard").innerHTML = buildKeyboard(layout);
    setupKeys();
    adjustHints("this");
}

document.addEventListener('DOMContentLoaded', init);