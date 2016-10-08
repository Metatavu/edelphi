(function() {
  'use strict';
  
  var WebSocketHandler = Class.create({
    initialize : function() {
      this._dispatchWebSocketMessageListener = this._onDispatchWebSocketMessage.bindAsEventListener(this);
      Event.observe(document, 'ws:dispatchMessage', this._dispatchWebSocketMessageListener);
      
      this._socketOpen = true;
      this._queue = [];
    },
    
    connect: function () {
      var host = window.location.host;
      var secure = location.protocol == 'https:';
      this._webSocket = this._createWebSocket((secure ? 'wss://' : 'ws://') + host + '/ws/socket/query');
      
      this._webSocket.onmessage = this._onWebSocketMessage.bind(this);
      this._webSocket.onerror = this._onWebSocketError.bind(this);
      this._webSocket.onclose = this._onWebSocketClose.bind(this);
      
      switch (this._webSocket.readyState) {
        case this._webSocket.CONNECTING:
          this._webSocket.onopen = this._onWebSocketOpen.bind(this);
        break;
        case this._webSocket.OPEN:
          this._onWebSocketOpen(this);
        break;
      }
    },
    
    disconnect: function () {
      this._webSocket.onclose = function () {};
      this._webSocket.close();
    },
    
    sendMessage: function (message) {
      if (this._socketOpen) {
        this._webSocket.send(JSON.stringify(message));
      } else {
        this._queue.push(message);
      }
    },
    
    _createWebSocket: function (url) {
      if ((typeof window.WebSocket) !== 'undefined') {
        return new WebSocket(url);
      } else if ((typeof window.MozWebSocket) !== 'undefined') {
        return new MozWebSocket(url);
      }
      
      return null;
    },
    
    _onDispatchWebSocketMessage: function (event) {
      var message = event.memo;
      this.sendMessage(message);
    },
    
    _onWebSocketMessage: function (event) {
      var message = JSON.parse(event.data);
      
      $(document).fire('ws:message', {
        data: message.data
      });
    },
    
    _onWebSocketOpen: function () {
      this._socketOpen = true;
      
      while (this._socketOpen && this._queue.length) {
        var message = this._queue.shift();
        this.sendMessage(message);
      }
      
    },
    
    _onWebSocketError: function (event) {
      
    },
    
    _onWebSocketClose: function () {
      this._socketOpen = false;
    }
    
  });

  var webSocketHandler = new WebSocketHandler();

  Event.observe(document, 'dom:loaded', function () {
    webSocketHandler.connect();
  });
  
  Event.observe(window, "unload", function (event) {
    webSocketHandler.disconnect();
  });
 
}).call(this);