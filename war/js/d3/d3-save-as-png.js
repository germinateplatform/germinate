/*
 *  Copyright 2018 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function() {
  var out$ = typeof exports != 'undefined' && exports || this;

  var doctype = '<?xml version="1.0" standalone="no"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">';

  function inlineImages(callback) {
    var images = document.querySelectorAll('svg image');
    var left = images.length;
    if (left == 0) {
      callback();
    }
    for (var i = 0; i < images.length; i++) {
      (function(image) {
        if (image.getAttribute('xlink:href')) {
          var href = image.getAttribute('xlink:href').value;
          if (/^http/.test(href) && !(new RegExp('^' + window.location.host).test(href))) {
            throw new Error("Cannot render embedded images linking to external hosts.");
          }
        }
        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext('2d');
        var img = new Image();
        img.src = image.getAttribute('xlink:href');
        img.onload = function() {
          canvas.width = img.width;
          canvas.height = img.height;
          ctx.drawImage(img, 0, 0);
          image.setAttribute('xlink:href', canvas.toDataURL('image/png'));
          left--;
          if (left == 0) {
            callback();
          }
        }
      })(images[i]);
    }
  }

  function styles(dom) {
    var css = "";
    var sheets = document.styleSheets;
    for (var i = 0; i < sheets.length; i++) {
      try {
        if(sheets[i].cssRules)
        {
          var rules = sheets[i].cssRules;
          for (var j = 0; j < rules.length; j++) {
            var rule = rules[j];
            if (typeof(rule.style) != "undefined") {
              css += rule.selectorText + " { " + rule.style.cssText + " }\n";
            }
          }
        }
      }
      catch (e) {
        if(e.name !== 'SecurityError')
          throw e;
      }
    }

    var s = document.createElement('style');
    s.setAttribute('type', 'text/css');
    s.innerHTML = "<![CDATA[\n" + css + "\n]]>";

    var defs = document.createElement('defs');
    defs.appendChild(s);
    return defs;
  }

  function styles2(dom) {
    var css = "";
    var sheets = document.styleSheets;
    for (var i = 0; i < sheets.length; i++) {
      try {
        if(sheets[i].cssRules)
        {
          var rules = sheets[i].cssRules;
          for (var j = 0; j < rules.length; j++) {
            var rule = rules[j];
            if (typeof(rule.style) != "undefined") {
              css += rule.selectorText + " { " + rule.style.cssText + " }\n";
            }
          }
        }
      }
      catch (e) {
        if(e.name !== 'SecurityError')
          throw e;
      }
    }

    var s = document.createElement('style');
    s.setAttribute('type', 'text/css');
    s.innerHTML = css;

    var defs = document.createElement('defs');
    defs.appendChild(s);
    return defs;
  }

  out$.svgAsBlob = function(el, scaleFactor, cb) {
    scaleFactor = scaleFactor || 1;

    inlineImages(function() {
      var outer = document.createElement("div");
      var clone = el.cloneNode(true);
      var width = parseInt(
          clone.getAttribute('width')
          || clone.style.width
          || out$.getComputedStyle(el).getPropertyValue('width')
      );
      var height = parseInt(
          clone.getAttribute('height')
          || clone.style.height
          || out$.getComputedStyle(el).getPropertyValue('height')
      );

      var xmlns = "http://www.w3.org/2000/xmlns/";

      clone.setAttribute("version", "1.1");
      clone.setAttributeNS(xmlns, "xmlns", "http://www.w3.org/2000/svg");
      clone.setAttributeNS(xmlns, "xmlns:xlink", "http://www.w3.org/1999/xlink");
      clone.setAttribute("width", width * scaleFactor);
      clone.setAttribute("height", height * scaleFactor);
      clone.setAttribute("viewBox", "0 0 " + width + " " + height);
      outer.appendChild(clone);

      clone.insertBefore(styles2(clone), clone.firstChild);

      // Create a blob from the SVG data
      var svgData = new XMLSerializer().serializeToString(clone);
      var blob = new Blob([svgData], { type: "image/svg+xml;charset=utf-8" });

      // Get the blob's URL
      var blobUrl = (self.URL || self.webkitURL || self).createObjectURL(blob);

      if (cb) {
        cb(blobUrl);
      }
    });
  };

  out$.svgAsDataUri = function(el, scaleFactor, cb) {
    scaleFactor = scaleFactor || 1;

    inlineImages(function() {
      var outer = document.createElement("div");
      var clone = el.cloneNode(true);
      var width = parseInt(
        clone.getAttribute('width')
          || clone.style.width
          || out$.getComputedStyle(el).getPropertyValue('width')
      );
      var height = parseInt(
        clone.getAttribute('height')
          || clone.style.height
          || out$.getComputedStyle(el).getPropertyValue('height')
      );

      var xmlns = "http://www.w3.org/2000/xmlns/";

      clone.setAttribute("version", "1.1");
      clone.setAttributeNS(xmlns, "xmlns", "http://www.w3.org/2000/svg");
      clone.setAttributeNS(xmlns, "xmlns:xlink", "http://www.w3.org/1999/xlink");
      clone.setAttribute("width", width * scaleFactor);
      clone.setAttribute("height", height * scaleFactor);
      clone.setAttribute("viewBox", "0 0 " + width + " " + height);
      outer.appendChild(clone);

      clone.insertBefore(styles(clone), clone.firstChild);

      var svg = doctype + outer.innerHTML;
      var uri = 'data:image/svg+xml;base64,' + window.btoa(unescape(encodeURIComponent(svg)));
      if (cb) {
        cb(uri);
      }
    });
  };

  out$.saveSvgAsPng = function(el, name, scaleFactor) {
    out$.svgAsDataUri(el, scaleFactor, function(uri) {
      var image = new Image();
      image.src = uri;
      image.onload = function() {
        var canvas = document.createElement('canvas');
        canvas.width = image.width;
        canvas.height = image.height;
        var context = canvas.getContext('2d');
        context.drawImage(image, 0, 0);

        var a = document.createElement('a');
        a.download = name;
        a.href = canvas.toDataURL('image/png');
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      }
    });
  }

  out$.saveLegendAsPng = function(e1, name) {
    var data = {};

    data.overflow = e1.style.overflowY;
    data.height = e1.style.height;
    data.maxHeight = e1.style.maxHeight;

    e1.style.overflowY = "visible";
    e1.style.height = e1.scrollHeight + "px";
    e1.style.maxHeight = e1.scrollHeight + "px";

    html2canvas(e1, {
      "onrendered": function (canvas) {
        console.log(canvas);

        var a = document.createElement('a');
        a.download = name;
        a.href = canvas.toDataURL('image/png');
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        e1.style.overflowY = data.overflow;
        e1.style.height = data.height;
        e1.style.maxHeight = data.maxHeight;
      }
    });
  }

})();
