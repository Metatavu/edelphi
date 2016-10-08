(function() {
  'use strict';
  
  $.widget("custom.liveQuery", {
    _create : function() {
      this._pageData = JSON.parse( $(this.element).attr('data-page-data') );
      $(this.element).removeAttr('data-page-data');
      $(this.element).attr('id', 'queryPage-' + this._pageData.queryPageId);
      
      this._values = this._pageData.values;
      this._layout = {
        showlegend: false,
        xaxis: { 
          title: this._pageData.xLabel,
          titlefont: {
            family: 'PT Sans",Arial,sans-serif',
            size: 15,
            color: '#df4d1c'
          },
          range: [0, 6]
        },
        yaxis: { 
          title: this._pageData.yLabel,
          titlefont: {
            family: 'PT Sans",Arial,sans-serif',
            size: 15,
            color: '#df4d1c'
          },
          range: [0, 6] 
        }
      };
      
      this._config = {
        staticPlot: true,
        scrollZoom: true,
        showTips: false,
        editable: false,
        displayModeBar: false,
        displaylogo: false,
        autosizable: false
      };
      
      this._traces = this._createTraces();
      Plotly.newPlot(this.element[0], this._traces, this._layout, this._config);
    },
    
    updateValues: function (replyId, values) {
      this._values[replyId] = values;
      this._traces = this._createTraces();
      Plotly.newPlot(this.element[0], this._traces, this._layout);
    },
    
    _createTraces: function () {
      var flatten = this._flattenValues();
      
      var traces = [];
      
      for (var x = 0; x < flatten.length; x++) {
        var xValues = new Array(7).fill(this._pageData.xTickLabels[x]);
        var yValues = this._pageData.yTickLabels;
        var sizes = new Array(7).fill(0);
        
        for (var y = 0; y < flatten[x].length; y++) {
          sizes[y] = flatten[x][y] * 20;
        }
        
        var trace = {
          x: xValues,
          y: yValues,
          mode: 'markers',
          marker: {
            size: sizes
          },
          type: "scatter"
        };
        
        traces.push(trace);
      }
      
      return traces;
    },
    
    _flattenValues: function () {
      var result = new Array(7);
      
      for (var x = 0; x < 7; x++) {
        result[x] = new Array(7);
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
    
    _createQuery1Charts: function () {
      
      var sizes = [];
      
      for (var x = -3; x < 3; x++) {
        for (var y = -3; y < 3; y++) {
          sizes.push(x + y + 10 * 10);
        }        
      }
      
      var trace1 = {
          x: [-3, -2, -1, 0, 1, 2, 3],
          y: [-3, -2, -1, 0, 1, 2, 3],
          mode: 'markers',
          marker: {
//            color: ["hsl(0,100,40)", "hsl(33,100,40)", "hsl(66,100,40)", "hsl(99,100,40)"],
            size: sizes
//            opacity: [0.6, 0.7, 0.8, 0.9]
          },
          type: "scatter"
        };

        var data = [trace1];

        var layout = {
          showlegend: false,
          xaxis: { range: [-3, 3] },
          yaxis: { range: [-3, 3] }
        };
        
        var config = {
          staticPlot: true,
          scrollZoom: true,
          showTips: false,
          editable: false,
          displayModeBar: false,
          displaylogo: false,
          autosizable: false
        };

        Plotly.newPlot($('#query-1 .main-chart')[0], data, layout, config);
        /**
        
        setInterval($.proxy(function() {
          trace1.marker.size[Math.round(Math.random() * trace1.marker.size.length)] += (Math.random() * 2) * 10;
          console.log(trace1.marker.size);
          for (var i = 0, l = trace1.x.length; i < l; i++) {
            trace1.x[i] += (Math.random() * 2) - 1;
          }
          
          for (var i = 0, l = trace1.y.length; i < l; i++) {
            trace1.y[i] += (Math.random() * 2) - 1;
          }
          Plotly.update($('#query-1 .main-chart')[0], data, layout);
        }, this), 300)
          **/
      
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