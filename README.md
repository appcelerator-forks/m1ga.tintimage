## Titanium module to tint an image

Binary inside android/dist/ folder

![ScreenShot](https://raw.github.com/m1ga/tintimage/master/android/example/demo.jpg)

### Attention:
Android ImageView/Button.tintColor() will be available in Titanium 5.4.0! 

### Functions:

* tint({ image: blob, mask: blob, mode: string, modeMask: string });
* mask({ image: blob, mask: blob });
* grayscale({ image: blob });

```javascript
var tint = require("miga.tintimage");

var blob1 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'bild1.jpg').read();
var blob2 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'bild3.png').read();
var blob3 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'bild2.jpg').read();

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
    modeMask: "overlay" // overlay mode for "mask"
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
```

Modes:
```
  add  		Saturate(S + D)  
  clear  	[0, 0]  
  darken  	[Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + min(Sc, Dc)]  
  dst  		[Da, Dc]  
  dst_atoP  	[Sa, Sa * Dc + Sc * (1 - Da)]  
  dst_in  	[Sa * Da, Sa * Dc]  
  dst_out  	[Da * (1 - Sa), Dc * (1 - Sa)]  
  dst_oveR  	[Sa + (1 - Sa)*Da, Rc = Dc + (1 - Da)*Sc]  
  lighten  	[Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + max(Sc, Dc)]  
  multiplY  	[Sa * Da, Sc * Dc]  
  overlay  	 
  screen  	[Sa + Da - Sa * Da, Sc + Dc - Sc * Dc]  
  src  		[Sa, Sc]  
  src_atoP  	[Da, Sc * Da + (1 - Sa) * Dc]  
  src_in  	[Sa * Da, Sc * Da]  
  src_out  	[Sa * (1 - Da), Sc * (1 - Da)]  
  src_oveR  	[Sa + (1 - Sa)*Da, Rc = Sc + (1 - Sa)*Dc]  
  xor  		[Sa + Da - 2 * Sa * Da, Sc * (1 - Da) + (1 - Sa) * Dc]  
```
  see http://developer.android.com/reference/android/graphics/PorterDuff.Mode.html
