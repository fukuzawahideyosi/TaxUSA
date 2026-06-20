// 轻量 fallback：当 CDN jQuery 加载失败时，提供本页面用到的少量 jQuery 风格方法。
(function(window){
  function J(nodes){ this.nodes = Array.prototype.slice.call(nodes || []); }
  function $(selector){
    if (typeof selector === 'function') { document.addEventListener('DOMContentLoaded', selector); return new J([]); }
    if (selector instanceof Node) return new J([selector]);
    if (selector instanceof NodeList || Array.isArray(selector)) return new J(selector);
    return new J(document.querySelectorAll(selector));
  }
  J.prototype.each = function(fn){ this.nodes.forEach(function(n,i){ fn.call(n,i,n); }); return this; };
  J.prototype.text = function(v){ if(v===undefined) return this.nodes[0] ? this.nodes[0].textContent : ''; return this.each(function(){ this.textContent = v; }); };
  J.prototype.html = function(v){ if(v===undefined) return this.nodes[0] ? this.nodes[0].innerHTML : ''; return this.each(function(){ this.innerHTML = v; }); };
  J.prototype.val = function(v){ if(v===undefined) return this.nodes[0] ? this.nodes[0].value : ''; return this.each(function(){ this.value = v; }); };
  J.prototype.append = function(v){ return this.each(function(){ if(typeof v === 'string') this.insertAdjacentHTML('beforeend', v); else this.appendChild(v.cloneNode(true)); }); };
  J.prototype.empty = function(){ return this.each(function(){ this.innerHTML = ''; }); };
  J.prototype.show = function(){ return this.each(function(){ this.style.display = ''; }); };
  J.prototype.hide = function(){ return this.each(function(){ this.style.display = 'none'; }); };
  J.prototype.toggle = function(show){ return this.each(function(){ this.style.display = show === undefined ? (this.style.display === 'none' ? '' : 'none') : (show ? '' : 'none'); }); };
  J.prototype.addClass = function(c){ return this.each(function(){ this.classList.add(c); }); };
  J.prototype.removeClass = function(c){ return this.each(function(){ this.classList.remove(c); }); };
  J.prototype.on = function(evt, selector, handler){
    if (typeof selector === 'function') { handler = selector; selector = null; }
    return this.each(function(){
      this.addEventListener(evt, function(e){
        if (!selector) return handler.call(e.target, e);
        var t = e.target.closest(selector); if (t) handler.call(t, e);
      });
    });
  };
  J.prototype.attr = function(name, value){ if(value===undefined) return this.nodes[0] ? this.nodes[0].getAttribute(name) : null; return this.each(function(){ this.setAttribute(name,value); }); };
  J.prototype.find = function(sel){ var arr=[]; this.each(function(){ arr = arr.concat(Array.prototype.slice.call(this.querySelectorAll(sel))); }); return new J(arr); };
  J.prototype.closest = function(sel){ return this.nodes[0] ? new J([this.nodes[0].closest(sel)]) : new J([]); };
  J.prototype.next = function(sel){ var arr=[]; this.each(function(){ var n=this.nextElementSibling; if(n && (!sel || n.matches(sel))) arr.push(n); }); return new J(arr); };
  J.prototype.is = function(sel){ if(!this.nodes[0]) return false; if(sel === ':checked') return !!this.nodes[0].checked; return this.nodes[0].matches(sel); };
  window.jQuery = window.$ = $;
})(window);
