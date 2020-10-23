(function (w) {
    function ruphy() {
        this.prototype.extend = function () {
            var arg = arguments || [];
            var src = arg[0] || this;
            for (var i = 1; i < arg.length; i++) {
                if (typeof arg[i] === 'object') {
                    for (let k in arg[i]) {
                        src.prototype[k] = src.prototype[k] || arg[i][k];
                    }
                }
            }
        }
        ruphy.prototype.param = function (obj) {
            obj = obj || {};
            if (typeof obj === 'string') {
                return obj;
            }
            var pa = [];
            for (let k in obj) {
                if (typeof obj[k] !== 'function') {
                    p.push(k + '=' + (obj[k].join && obj.join(',') || obj[k] || ''));
                }
            }
            return pa.join('&');
        }
        ruphy.prototype.each = function (obj, callback) {
            callback = callback || function () {
                return true
            }
            obj = obj || [];
            if (typeof obj === 'object') {
                for (let k in obj) {
                    if (callback.call(obj[k], obj[k], k, obj) === false) {
                        break;
                    }
                }
            }
        }
        ruphy.prototype.post = function (url, params, func, type) {
            type = type || 'application/json';
            params = param(params);
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = c => {
                if (c.target.readyState == 4)
                    console.log(c.target.responseText);
                func && func(c.target.responseText);
            }
            xhr.open('POST', url, true);
            xhr.setRequestHeader('Content-Type', type + '; charset=UTF-8');
            xhr.send(params);
        }
        ruphy.prototype.get = function (url, func) {
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = c => {
                if (c.target.readyState == 4)
                    console.log(c.target.responseText);
                func && func(c.target.responseText);
            }
            xhr.open('GET', url, true);
            xhr.send();
        }
    }
    w._ = w.ruphy = ruphy;
})(window)

