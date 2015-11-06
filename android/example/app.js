var tint = require("miga.tintimage");

var blob1 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'bild1.jpg').read();
var blob2 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'bild3.png').read();
var blob3 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'bild2.png').read();

// mask image
$.img_final1.image = tint.mask({
    image: blob1,
    mask: blob2
});

// mask and tint
$.img_final2.image = tint.tint({
    image: blob1,
    mask: blob3,
    color: "#f04f0f",
    mode: "multiply",   // overlay mode for "color"
    modeMask: "multiply" // overlay mode for "mask"
});

// tint
$.img_final3.image = tint.tint({
    image: blob1,
    color: "#ff0000",
    mode: "multiply"
});

// tint - using mask as backgroundimage (like Ti.UI.MaskedImage)
$.img_final4.image = tint.tint({
    mask: blob1,
    color: "#ff00ff",
    mode: "multiply"
});

$.index.open();
