(function() {
  'use strict';
  
  $.widget("custom.bubbleGrid", {
    
    options: {
      columns: 7,
      rows: 7,
      marginX: 50,
      marginY: 50,
      labelX: '',
      labelY: '',
      labelMargin: 10,
      xTickLabels: null,
      yTickLabels: null,
      tickMarginY: 10,
      tickMarginX: 10
    },
    
    _allocateArray: function (size, value) {
      var result = [];
      
      for (var i = 0; i < size; i++) {
        result.push(value);
      }
      
      return result;
    },
    
    _create : function() {
      this.element.addClass('bubbleGrid');
      
      this._bubbleSpaceX = (this.element.outerWidth() - (this.options.marginX * 2)) / (this.options.columns - 1); 
      this._bubbleSpaceY = (this.element.outerHeight() - (this.options.marginY * 2)) / (this.options.rows - 1);
      this._createBubbles();
      this._createTickLabels();
      
      var labelX = $('<label>')
        .css({
          'position': 'absolute',
          'text-align': 'center'
        })
        .text(this.options.labelX)
        .addClass('axisLabel labelX')
        .appendTo(this.element);
      
      labelX
        .css({
          'top': this.options.labelMargin,
          'left': ((this.element.width() / 2) - (labelX.width() / 2))
        });
      
      var labelY = $('<label>')
        .css({
          'position': 'absolute',
          'transform': 'rotate(270deg)'
        })
        .text(this.options.labelY)
        .addClass('axisLabel labelY')
        .appendTo(this.element);
      
      labelY
        .css({
          'left': (-labelX.width() / 2) + this.options.labelMargin,
          'top': (this.element.height() / 2) - (labelX.height() / 2)
        });
    },
    
    bubbleSize: function (x, y, value) {
      $(this._bubbles[x][y]).css({
        'padding': value||1,
        'margin-left': -value||1,
        'margin-top': -value||1
      });
    },
    
    _createBubbles: function () {
      var colorMulX = 255 / this.options.rows;
      var colorMulY = 255 / this.options.columns;
      
      this._bubbles = this._allocateArray(this.options.column + 1);
      for (var x = 0; x < this.options.columns; x++) {
        this._bubbles[x] = this._allocateArray(this.options.rows + 1);
        for (var y = 0; y < this.options.columns; y++) {
          this._bubbles[x][y] = $('<div>')
            .css({
              top: (y * this._bubbleSpaceY) + this.options.marginY,
              left: (x * this._bubbleSpaceX) + this.options.marginX,
              background: 'rgb(' + [Math.round(x * colorMulX), 0, Math.round(y * colorMulY)].join(',') + ')'
            })
            .addClass('bubble')
            .appendTo(this.element);
        }        
      }
    },
    
    _createTickLabels: function () {
      if (this.options.xTickLabels && this.options.xTickLabels.length == this.options.columns) {
        for (var x = 0; x < this.options.columns; x++) {
          var tickLabelX = $('<label>')
            .css({
              'position': 'absolute',
              'text-align': 'center'
            })
            .text(this.options.xTickLabels[x])
            .addClass('tickLabel tickLabelX')
            .appendTo(this.element);
          
          tickLabelX.css({
            bottom: this.options.tickMarginY,
            left: ((x * this._bubbleSpaceX) + this.options.marginX) - (tickLabelX.width() / 2)
          })
        }
      }
      
      if (this.options.yTickLabels && this.options.yTickLabels.length == this.options.rows) {
        for (var y = 0; y < this.options.rows; y++) {
          var tickLabelY = $('<label>')
            .css({
              'position': 'absolute',
              'text-align': 'center'
            })
            .text(this.options.xTickLabels[y])
            .addClass('tickLabel tickLabelY')
            .appendTo(this.element);
          
          tickLabelY.css({
            right: this.options.tickMarginX,
            top: ((y * this._bubbleSpaceY) + this.options.marginY) - (tickLabelY.height() / 2)
          })
        }
      }
    }
    
  });
  
  $.widget("custom.liveQuery", {
    
    options: {
      columns: 7,
      rows: 7,
      valueSize: 15
    },
    
    _create : function() {
      this._pageData = JSON.parse( $(this.element).attr('data-page-data') );
      $(this.element).removeAttr('data-page-data');
      $(this.element).attr('id', 'queryPage-' + this._pageData.queryPageId);
      
      $('<h3>')
        .text(this._pageData.pageTitle)
        .appendTo(this.element);
      
      $('<div>')
        .appendTo(this.element)
        .bubbleGrid({
          labelX: this._pageData.xLabel,
          labelY: this._pageData.yLabel,
          xTickLabels: this._pageData.xTickLabels,
          yTickLabels: this._pageData.yTickLabels
        });

      this._values = this._pageData.values;
      this._refreshValues();
    },
    
    updateValues: function (replyId, values) {
      this._values[replyId] = values;
      this._refreshValues();
    },
    
    _refreshValues: function () {
      this._flatValue = this._flattenValues();
      
      for (var x = 0; x < this.options.rows; x++) {
        for (var y = 0; y < this.options.columns; y++) {
          this.element.find('.bubbleGrid').bubbleGrid('bubbleSize', x, y, this._flatValue[x][y] * this.options.valueSize)
        }
      }
    },
    
    _flattenValues: function () {
      var result = this._allocateArray(7);
      
      for (var x = 0; x < 7; x++) {
        result[x] = this._allocateArray(7);
        for (var y = 0; y < 7; y++) {
          result[x][y] = 0;;
        }        
      }
      
      _.each(this._values, function (value) {
        var x = Math.min(Math.max(parseInt(value[0]), 0), 6);
        var y = Math.min(Math.max(parseInt(value[1]), 0), 6);
        result[x][y]++;
      });
      
      return result;
    },
    
    _allocateArray: function (size) {
      var result = [];
      
      for (var i = 0; i < size; i++) {
        result.push(null);
      }
      
      return result;
    }
    
  });
  
  $.widget("custom.liveReport", {
    _create : function() {
      var host = window.location.host;
      var secure = location.protocol == 'https:';
      this._webSocket = this._createWebSocket((secure ? 'wss://' : 'ws://') + host + '/ws/socket/live'); 
      this._webSocket.onmessage = $.proxy(this._onWebSocketMessage, this);
      
      this._createCharts();
    },
    
    _createCharts: function () {
      $('.query').liveQuery();
    },
    
    _onWebSocketMessage: function (message) {
      var data = JSON.parse(message.data);
      
      var queryPageId = data.queryPageId;
      var replyId = data.replyId;
      var values = data.values;
      
      $('#queryPage-' + queryPageId).liveQuery('updateValues', replyId, values);
    },
    
    _createWebSocket: function (url) {
      if ((typeof window.WebSocket) !== 'undefined') {
        return new WebSocket(url);
      } else if ((typeof window.MozWebSocket) !== 'undefined') {
        return new MozWebSocket(url);
      }
      
      return null;
    },
    
    
  });

  $(document).ready(function () {
    $(document).liveReport();
  });
 
}).call(this);