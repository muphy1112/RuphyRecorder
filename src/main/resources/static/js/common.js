(function (w) {

    function ruphy(selector, content) {
        return new ruphy.fn.init(selector, content);
    }

    ruphy.fn = ruphy.prototype = {
        constructor: ruphy,
        init: function (selector, content) {
            if (!selector) {
                return this;
            }
            this.a = document.getElementById(selector);
            return this;
        }
    }

    ruphy.extend = ruphy.fn.extend = function () {
        let arg = [].concat.apply([], arguments) || [];
        let src = arg.length > 1 ? arg.splice(0, 1)[0] : this;
        for (let i = 0; i < arg.length; i++) {
            for (let k in arg[i]) {
                src[k] = src[k] || arg[i][k];
            }
        }
        return this;
    }

    ruphy.extend({
        responseProcess: function (c, func, resolve, reject) {
            let contentType = c.target.getResponseHeader('Content-Type');
            let responseText = c.target.responseText;
            if (contentType === 'application/json') {
                responseText = JSON.parse(responseText);
            }
            console.log(responseText);
            func && func.call(responseText, responseText, c.target.status); // 回调函数存在时调用回调函数，返回数据和状态
            if (c.target.status < 300) {
                resolve && resolve(responseText); // 成功时调用resolve函数
            } else {
                reject && reject(responseText);   // 失败时调用reject函数
            }
        },
        getQueryString: function (url) {
            url = url || location.href;
            if (!/\?/.test(url)) {
                return '';
            }
            return url.replace(/[^\?]+\?([^\#]+)/, '$1');
        },
        getHashString: function (url) {
            url = url || location.href;
            if (!/\#/.test(url)) {
                return '';
            }
            return url.replace(/[^\#]+\#(.+)/, '$1');
        },
        getQueryParameter: function (url, name) {
            let queryStr = this.getQueryString(url);
            let obj = this.paramObj(queryStr);
            return !!name ? obj[name] || '' : obj;
        },
        getHashParameter: function (url, name) {
            let hash = this.getHashString(url);
            let obj = this.paramObj(hash);
            return !!name ? obj[name] || '' : obj;
        },
        paramObj: function (param) {
            let kvs = param.split('&');
            let obj = {};
            for (let i = 0; i < kvs.length; i++) {
                let kv = kvs[i].split("=");
                if (!kv[0]) {
                    continue;
                }
                if (!kv[1]) {
                    obj[kv[0]] = obj[kv[0]] || '';
                    continue;
                }
                if (obj[kv[0]] === kv[1]) {
                    obj[kv[0]] = obj[kv[0]] || '';
                    continue;
                }
                obj[kv[0]] = obj[kv[0]] && [obj[kv[0]], kv[1]] || kv[1];
            }
            return obj;
        },
        param: function (obj) {
            obj = obj || {};
            if (typeof obj === 'string') {
                return obj;
            }
            let pa = [];
            for (let k in obj) {
                if (typeof obj[k] !== 'function') {
                    p.push(k + '=' + (obj[k].join && obj.join(',') || obj[k] || ''));
                }
            }
            return pa.join('&');
        },
        post: function (url, params, func) {
            if (typeof params === 'function') {
                func = params;
                params = undefined;
            }
            let that = this;
            return new Promise(function (resolve, reject) {
                var xhr = window.XMLHttpRequest && new XMLHttpRequest() || new ActiveXObject();
                xhr.onreadystatechange = c => {
                    if (c.target.readyState == 4) {
                        that.responseProcess(c, func, resolve, reject);
                    }
                };
                xhr.open('POST', url, true);
                if (typeof params === 'string') {
                    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
                    xhr.send(params);
                } else if (params instanceof FormData) {
                    xhr.send(params);
                } else {
                    // xhr.setRequestHeader('Content-Type', 'multipart/form-data; charset=UTF-8'); 不需要设置
                    var fd = new FormData();
                    for (var k in params) {
                        typeof params[k] !== 'function' && fd.append(k, params[k]);
                    }
                    xhr.send(fd);
                }
            });
        },
        get: function (url, params, func) {
            if (typeof params === 'function') {
                func = params;
                params = undefined;
            }
            if (params) {
                url += /\?/.test(url) ? '&' : '?';
                if (typeof params !== 'string') {
                    for (var k in params) {
                        url += k + '=' + params[k] + '&';
                    }
                } else {
                    url += params;
                }
            }
            let that = this;
            return new Promise(function (resolve, reject) {
                var xhr = window.XMLHttpRequest && new XMLHttpRequest() || new ActiveXObject();
                xhr.onreadystatechange = c => {
                    if (c.target.readyState == 4) {
                        that.responseProcess(c, func, resolve, reject);
                    }
                };
                xhr.open('GET', url, true);
                xhr.send();
            });
        },
        each: function (arg, fun) {
            arg = arg || {};
            fun = fun || function () {
                return false;
            }
            for (let k in arg) {
                if (fun.call(arg[k], arg[k], k, arg) === false) {
                    break;
                }
            }
            return this;
        },
        isObject: function (obj) {
            return typeof obj === 'object' && /^\{/.test(JSON.stringify(obj));
        },
        isNull: function (e) {
            if (typeof e === 'number') {
                return b !== b;
            }
            return !(typeof e === 'boolean' || e);
        },
        isEmpty: function (e) {
            return this.isNull(e) || (e.length && e.length === 0) || (this.isObject(e) && /^\{\}/.test(e));
        },
        nvl: function (e, d) {
            return this.isNull(e) ? d : e;
        }
    });

    ruphy.fn.extend({});

    ruphy.fn.init.prototype = ruphy.fn;
    w._ = w.ruphy = ruphy;
})(window);

(function (w, _) {
    let util = _.util || {};
    util.table = util.table || {};
    _.extend(util.table, {
        create: function (selector, data, head, cft) {
            cft = cft || {};
            data = _.isObject(data) && [data] || data || [];
            selector = selector instanceof HTMLElement ? selector : selector &&
                document.getElementById(selector) || document.getElementsByTagName('body')[0];
            head = head instanceof Array && head || head && this.getHeads(head, true) || this.getHeads(data);
            let thead = this.getThead(head, function (e) {
                e.style.backgroundColor = '#def';
            }, function (e) {
                e.style.border = '1px solid #ccc';
            });
            let tbody = this.getTbody(data, head, function (e) {
                e.style.backgroundColor = e.className.indexOf('tr-0') != -1 ? '#a9a2a25c' : "#a9a2a236";
            }, function (e) {
                e.style.border = '1px solid #ccc';
            });
            let table = document.createElement('table');
            table.style.width = '100%';
            table.appendChild(thead);
            table.appendChild(tbody);
            let dt = document.createElement('div');
            dt.style.minHeight = '100px';
            dt.style.maxHeight = '460px';
            dt.style.overflow = 'auto';
            dt.appendChild(table);
            let dh = document.createElement('div');
            dh.style.fontSize = '20px';
            let dhs = document.createElement('span');
            dhs.innerText = cft.title || '提示信息';
            dh.appendChild(dhs);
            let df = document.createElement('div');
            let dfs = document.createElement('span');
            dfs.innerText = '当前有' + data.length + '条记录！';
            df.appendChild(dfs);
            let content = document.createElement('div');
            content.style.margin = '10px 0';
            content.appendChild(dh);
            content.appendChild(dt);
            content.appendChild(df);
            selector.innerHTML = '';
            selector.appendChild(content);
            return this;
        },
        getHeads: function (e, useValue) {
            let heads = [];
            let obj = e;
            if (e instanceof Array && e.length > 0) {
                obj = e[0];
            }
            if (_.isObject(obj)) {
                for (let k in obj) {
                    if (heads.indexOf(k) < 0) {
                        useValue ? heads.push([k, obj[k] || k]) : heads.push([k, k]);
                    }
                }
            }
            return heads;
        },
        getThead: function (head, rowfun, colfun) {
            head = head || [];
            let thead = document.createElement('thead');
            let tr = this.getHeadRow(head, 1, rowfun, colfun);
            thead.appendChild(tr);
            return thead;
        },
        getTbody: function (data, head, rowfun, colfun) {
            let tbody = document.createElement('tbody');
            if (_.isEmpty(data)) {
                let tr = this.getBodyRow("没有数据！", head, 1, 1, function () {
                    rowfun && rowfun.call(this, this);
                    this.className = (this.className + " tr-empty").trim();
                }, colfun);
                tbody.appendChild(tr);
            } else {
                data = [].concat.call(data, []);
                for (let i in data) {
                    let tr = this.getBodyRow(data[i], head, i, 1, rowfun, colfun);
                    tbody.appendChild(tr);
                }
            }
            return tbody;
        },
        getBodyRow: function (row, head, rownum, rowspan, rowfun, colfun) {
            head = head || [[]];
            let tr = document.createElement('tr');
            if (_.isObject(row)) {
                for (let i = 0; i < head.length; i++) {
                    let td = this.getCol('td', row[head[i][0]], rownum, i, rowspan, 1, function () {
                        colfun && colfun.call(this, this);
                        head[i][2] && head[i][2].call(this, this);
                    });
                    tr.appendChild(td);
                }
            } else {
                let td = this.getCol('td', _.nvl(row, '没有数据！'), rownum, 1, rowspan, head.length, function () {
                    colfun && colfun.call(this, this);
                    // this.style = (this.style + " color: #dce; text-align: center;").trim();
                    this.className = (this.className + " td-empty").trim();
                });
                tr.appendChild(td);
            }
            tr.className = (tr.className + " tr tr-" + (rownum % 2)).trim();
            rowfun && rowfun.call(tr, tr);
            return tr;
        },
        getHeadRow: function (head, rowspan, rowfun, colfun) {
            head = head || [];
            let tr = document.createElement('tr');
            for (let i = 0; i < head.length; i++) {
                let td = this.getCol('th', head[i][1], 1, i, rowspan, 1, colfun);
                tr.appendChild(td);
            }
            tr.className = (tr.className + " tr").trim();
            rowfun && rowfun.call(tr, tr);
            return tr;
        },
        getCol: function (type, val, rownum, colnum, rowspan, colspan, colfun) {
            val = val || '';
            rowspan = rowspan || 1;
            colspan = colspan || 1;
            rownum = rownum || 0;
            colnum = colnum || 0;
            type = type || 'td';
            let col = document.createElement(type);
            col.colSpan = colspan;
            col.rowSpan = rowspan;
            col.title = val;
            col.id = type + "-" + rownum + '-' + colnum;
            col.className = (col.className + " " + type).trim();
            col.innerHTML = val;
            colfun && colfun.call(col, col);
            return col;
        }
    });
    _.util = util;
})(window, ruphy);