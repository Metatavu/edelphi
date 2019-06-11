(function() {
  'use strict';
  
  var messages = [];
  
  console.log = function () {
    messages.push(arguments);
  };
  
  window.addEventListener('load', () => {
    var container = new Element("div", {
      style: "color: #0a0; background: rgba(0, 0, 0, 0.9); position: fixed; left: 0;right: 0; bottom: 0; font: Courier New; word-break: break-all; z-index: 9999"
    });

    document.body.appendChild(container);

    console.log = function () {
      var log = new Element("div").update(JSON.stringify(arguments));
      container.appendChild(log);
    };
    
    window.onerror = function (msg, url, line) {
        console.log("Caught via window.onerror: '" + msg + "' from " + url + ":" + line);
    };

    for (var i = 0, l = messages.length; i < l; i++) {
      console.log(messages[i]);
    }
    
    $(document).on('click', '#log', function (event) {
      container.update("");
    }); 
    
  });

}).call(this);