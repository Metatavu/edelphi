/*global BlockController,QueryBlockFragmentController,getLocale,JSONUtils,startLoadingOperation,endLoadingOperation,JSDATA,ModalPopup,Flotr*/

var TextQueryPageController;
var TimeSerieQueryPageController;
var Scale1DQueryPageController;
var Scale2DQueryPageController;
var ExpertiseQueryPageController;
var MultiSelectQueryPageController;
var OrderQueryPageController;
var GroupingQueryPageController;
var TimeLineQueryPageController;
var Multiple2DScaleQueryPageController;
var FormQueryPageController;
var Collage2DQueryPageController;
var QueryBlockScaleSliderFragmentController;
var QueryBlockScaleGraphFragmentController;
var QueryBlockMultiSelectFragmentController;
var QueryCommentsController;
var QueryBubbleChartLiveReportController;
var QueryBlockScaleRadioListFragmentController;
var QueryBarChartLiveReportController;
var QueryPageController;
var QueryLiveReportController;

/**
 * Trigger event from legacy code into ReactJs
 * 
 * @param string command
 * @param object event data 
 */
function triggerReactCommand(command, data) {
  var event = new CustomEvent('react-command', { 
    bubbles: true,
    detail: {
      command: command,
      data: data || {}
    }
  });

  document.dispatchEvent(event);
}

var QueryBlockController = Class.create(BlockController, {
  initialize : function($super) {
     $super();

     this._toggleCommentsClickListener = this._onToggleCommentsClickListener.bindAsEventListener(this);
     this._sortCommentsAscendingTimeClickListener = this._onSortCommentsAscendingTimeClickListener.bindAsEventListener(this);
     this._sortCommentsDescendingTimeClickListener = this._onSortCommentsDescendingTimeClickListener.bindAsEventListener(this);

     this._currentPage = 0;
   },
          setup : function($super) {
            $super($('panelQueryBlock'));

            // Comment hash support
            var comment = parseInt(this.getQueryParam('comment'));
            if (!isNaN(comment)) {
              location.hash = "#comment." + comment;

              var commentsContainer = this.getBlockElement().down(
                  ".queryCommentsContainer");
              if (commentsContainer)
                commentsContainer.show();
            }

            var page = parseInt(this.getQueryParam('page'));
            if (!isNaN(page)) {
              this._currentPage = page;
            }

            this._pageType = this.getBlockElement().down(
                'input[name="queryPageType"]').value;

            this._commentsHeaderToggle = this.getBlockElement().down(
                ".queryCommentsShowHideToggle");

            if (this._commentsHeaderToggle)
              Event.observe(this._commentsHeaderToggle, "click",
                  this._toggleCommentsClickListener);

            this._sortTimeAscending = $('commentSortTimeAsc');
            if (this._sortTimeAscending)
              Event.observe(this._sortTimeAscending, "click",
                  this._sortCommentsAscendingTimeClickListener);

            this._sortTimeDescending = $('commentSortTimeDesc');
            if (this._sortTimeDescending)
              Event.observe(this._sortTimeDescending, "click",
                  this._sortCommentsDescendingTimeClickListener);

            switch (this._pageType) {
            case 'TEXT':
              this.queryPageController = new TextQueryPageController(this);
              break;
            case 'THESIS_TIME_SERIE':
              this.queryPageController = new TimeSerieQueryPageController(this);
              break;
            case 'THESIS_SCALE_1D':
              this.queryPageController = new Scale1DQueryPageController(this);
              break;
            case 'THESIS_SCALE_2D':
              this.queryPageController = new Scale2DQueryPageController(this);
              break;
            case 'EXPERTISE':
              this.queryPageController = new ExpertiseQueryPageController(this);
              break;
            case 'THESIS_MULTI_SELECT':
              this.queryPageController = new MultiSelectQueryPageController(
                  this);
              break;
            case 'THESIS_ORDER':
              this.queryPageController = new OrderQueryPageController(this);
              break;
            case 'THESIS_GROUPING':
              this.queryPageController = new GroupingQueryPageController(this);
              break;
            case 'THESIS_TIMELINE':
              this.queryPageController = new TimeLineQueryPageController(this);
              break;
            case 'THESIS_MULTIPLE_2D_SCALES':
              this.queryPageController = new Multiple2DScaleQueryPageController(
                  this);
              break;
            case 'FORM':
              this.queryPageController = new FormQueryPageController(this);
              break;
            case 'COLLAGE_2D':
              this.queryPageController = new Collage2DQueryPageController(this);
              break;
            default:
              break;
            }

            startPing();
          },

          deinitialize : function() {
            if (this._commentsHeaderToggle)
              Event.stopObserving(this._commentsHeaderToggle, "click",
                  this._toggleCommentsClickListener);
            if (this._sortTimeAscending)
              Event.stopObserving(this._sortTimeAscending, "click",
                  this._sortCommentsAscendingTimeClickListener);
            if (this._sortTimeDescending)
              Event.stopObserving(this._sortTimeDescending, "click",
                  this._sortCommentsDescendingTimeClickListener);
          },

          disableNext : function() { 
            triggerReactCommand("disable-query-next");
          },

          enableNext : function() {
            triggerReactCommand("enable-query-next");
          },
          
          _onToggleCommentsClickListener : function(event) {
            var element = Event.element(event);
            Event.stop(event);

            var commentsContainer = element.up(".queryCommentList").down(
                ".queryCommentsContainer");
            var commentsShowHideToggle = element.up(".queryCommentList").down(
                ".queryCommentsShowHideToggle");

            if (commentsContainer.visible()) {
              commentsContainer.fade();
              commentsShowHideToggle.removeClassName("hideIcon");
              commentsShowHideToggle.addClassName("showIcon");
            } else {
              commentsContainer.appear();
              commentsShowHideToggle.removeClassName("showIcon");
              commentsShowHideToggle.addClassName("hideIcon");
            }
          },

          _onSortCommentsAscendingTimeClickListener : function(event) {
            Event.stop(event);
            this._sortComments(true);
          },

          _onSortCommentsDescendingTimeClickListener : function(event) {
            Event.stop(event);
            this._sortComments(false);
          },

          _sortComments : function(ascending) {
            this.getBlockElement().select(
                '.queryCommentsContainer>.queryComment').sort(function(a, b) {
              var aId = 0;
              a.select("input[name='commentId']").each(function(element) {
                if (element.value > aId) {
                  aId = element.value;
                }
              });
              var bId = 0;
              b.select("input[name='commentId']").each(function(element) {
                if (element.value > bId) {
                  bId = element.value;
                }
              });
              return ascending ? bId - aId : aId - bId;
            }).each(Element.prototype.appendChild,
                this.getBlockElement().down('.queryCommentsContainer'));
          }
        });

addBlockController(new QueryBlockController());

QueryPageController = Class.create({
  initialize : function(blockController) {
    this._blockController = blockController;

    if ($('queryCommentList') != undefined) {
      this._commentControl = new QueryCommentsController();
      this._commentControl.setup();
    }
  },
  deinitialize : function() {
    if (this._commentControl)
      this._commentControl.deinitialize();
  },
  getBlockController : function() {
    return this._blockController;
  },
  getBlockElement : function() {
    return this.getBlockController().getBlockElement();
  }
});

TextQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);
  },
  deinitialize : function($super) {
    $super();
  }
});

TimeSerieQueryPageController = Class
    .create(
        QueryPageController,
        {
          initialize : function($super, blockController) {
            $super(blockController);

            var questionContainer = this.getBlockElement().down(
                ".queryQuestionContainer");

            this._timeSerieController = new QueryBlockTimeSerieFragmentController(
                questionContainer);

            var liveReportContainer = this.getBlockElement().down(
                '.queryLiveReportContainer');
            if (liveReportContainer) {
              this._timeSerieController.addListener("valueChange", this,
                  this._onTimeSerieValueChange);

              this._liveReportController = new QueryLineChartLiveReportController(
                  "report_linechart", liveReportContainer,
                  this._timeSerieController.getMinY(),
                  this._timeSerieController.getMaxY());
              this._replyCount = parseInt(this._liveReportController
                  ._retrieveFieldValue('replyCount'));
              this._preliminaryDataSetIndex = 0;
              this._averageDataSetIndex = 1;
              this._minDataSetIndex = 2;
              this._maxDataSetIndex = 3;

              this._setReportDataSerie(this._timeSerieController
                  .getUserDataSerie());
            }
          },
          deinitialize : function($super) {
            $super();

            this._timeSerieController.deinitialize();
          },
          _updateReport : function() {
            if (this._liveReportController) {
              this._liveReportController.draw();
            }
          },
          _setReportDataSerie : function(dataSerie) {
            var userData = new Array();
            var maxData = this._liveReportController
                .getData(this._maxDataSetIndex);
            var minData = this._liveReportController
                .getData(this._minDataSetIndex);
            var averageData = this._liveReportController
                .getData(this._averageDataSetIndex);

            var minX = this._timeSerieController.getMinX();
            var stepX = this._timeSerieController.getStepX();
            var userStepX = this._timeSerieController.getUserStepX();
            var stepGCD = Math.getGCD(stepX, userStepX);

            this._liveReportController.clearOverriddenValues();

            for (var serieIndex = 0, serieLength = dataSerie.length; serieIndex < serieLength; serieIndex++) {
              var x = (dataSerie[serieIndex][0] - minX) / stepGCD;
              var y = dataSerie[serieIndex][1];

              userData.push([ x, y ]);
              for (var i = 0, l = maxData.length; i < l; i++) {
                if (maxData[i][0] == x) {
                  if (maxData[i][1] < y) {
                    this._liveReportController.setOverriddenValue(
                        this._maxDataSetIndex, i, x, y);
                  }

                  break;
                }
              }

              for (var i = 0, l = minData.length; i < l; i++) {
                if (minData[i][0] == x) {
                  if (minData[i][1] > y) {
                    this._liveReportController.setOverriddenValue(
                        this._minDataSetIndex, i, x, y);
                  }

                  break;
                }
              }

              for (var i = 0, l = minData.length; i < l; i++) {
                if (averageData[i][0] == x) {
                  var oldAvg = averageData[i][1];
                  var newAvg = oldAvg * this._replyCount;
                  newAvg += y;
                  newAvg /= this._replyCount + 1;
                  this._liveReportController.setOverriddenValue(
                      this._averageDataSetIndex, i, x, newAvg);
                  break;
                }
              }
            }

            this._liveReportController.setUserData(userData);

            this._updateReport();
          },
          _onTimeSerieValueChange : function(event) {
            if (this._liveReportController) {
              var dataSerie = event.dataSerie;
              this._setReportDataSerie(dataSerie);
            }
          }
        });

Scale1DQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);

    this._max = NaN;
    this._min = NaN;
    this._selected = null;

    var questionContainer = this.getBlockElement().down(".queryQuestionContainer");
    
    if (questionContainer.hasClassName("queryScaleSliderQuestionContainer")) {
      this._initializeSlider(questionContainer);
    } else if (questionContainer.hasClassName("queryScaleRadioListQuestionContainer")) {
      this._initializeRadioList(questionContainer);
    }

    var liveReportContainer = this.getBlockElement().down('.queryLiveReportContainer');
    if (liveReportContainer) {
      this._liveReportController = new QueryBarChartLiveReportController("report_barchart", liveReportContainer, this._min, this._max, this._tickLabels);
      this._updateReport();
    }
  },

  deinitialize : function($super) {
    $super();
    if (this._sliderController)
      this._sliderController.deinitialize();
    if (this._radioListController)
      this._radioListController.deinitialize();
    if (this._liveReportController)
      this._liveReportController.deinitialize();
  },

  _initializeSlider : function(questionContainer) {
    this._sliderController = new QueryBlockScaleSliderFragmentController(
        questionContainer);
    this._sliderController.addListener("valueChange", this,
        this._onSliderValueChange);

    this._max = this._sliderController.getMax();
    this._min = this._sliderController.getMin();

    this._selected = this._sliderController.getSelected();
    this._tickLabels = this._sliderController.getValueLabels();

    if (this._selected === null) {
      this.getBlockController().disableNext();
    }
  },

  _initializeRadioList : function(questionContainer) {
    this._radioListController = new QueryBlockScaleRadioListFragmentController(questionContainer);
    this._radioListController.addListener("valueChange", this, this._onRadioListValueChange);

    this._max = this._radioListController.getMax();
    this._min = this._radioListController.getMin();
    this._selected = this._radioListController.getSelected();
    this._tickLabels = this._radioListController.getValueLabels();

    if (this._selected === null) {
      this.getBlockController().disableNext();
    }
  },

  _updateReport : function() {
    if (this._liveReportController) {
      for (var i = this._min, l = this._max + 1; i < l; i++) {
        this._liveReportController.setUserData(i, this._selected == i ? 1 : 0);
      }
      this._liveReportController.draw();
    }
  },

  _onSliderValueChange : function(event) {
    this._selected = event.value;
    if (this._selected !== null) {
      this.getBlockController().enableNext();
    }

    this._updateReport();
  },

  _onRadioListValueChange : function(event) {
    this._selected = event.value;
    if (this._selected !== null) {
      this.getBlockController().enableNext();
    }

    this._updateReport();
  }
});

Multiple2DScaleQueryPageController = Class
    .create(
        QueryPageController,
        {

          initialize : function($super, blockController) {
            $super(blockController);

            var labelCells = this.getBlockElement().select('td:first-child');

            var maxHeight = 0;
            labelCells.each(function(labelCell) {
              var cellLayout = new Element.Layout(labelCell);
              maxHeight = Math.max(maxHeight, cellLayout.get('height'));
            });

            labelCells.each(function(labelCell) {
              labelCell.setStyle({
                'height' : maxHeight + 'px'
              });
            });

            this._valueChangeListener = this._onValueChange
                .bindAsEventListener(this);
            this
                .getBlockElement()
                .select(
                    '.queryMultiple2DScalesQuestionContainer table input[type="radio"]')
                .each(function(input) {
                  $(input).observe("change", this._valueChangeListener);
                }.bind(this));

            this._updateNextButton();
          },

          deinitialize : function($super) {
            this
                .getBlockElement()
                .select(
                    '.queryMultiple2DScalesQuestionContainer table input[type="radio"]')
                .each(function(input) {
                  $(input).stopObserving("change", this._valueChangeListener);
                }.bind(this));

            $super();
          },

          _getOptionCount : function() {
            var table = this.getBlockElement().select(
                '.queryMultiple2DScalesQuestionContainer table')[0];
            return parseInt($(table).getAttribute('data-option-count'));
          },

          _getThesisCount : function() {
            var table = this.getBlockElement().select(
                '.queryMultiple2DScalesQuestionContainer table')[0];
            return parseInt($(table).getAttribute('data-thesis-count'));
          },

          _getSelectedThesisOption : function(thesisIndex, axis) {
            var inputName = 'multiple2dscales.' + thesisIndex + '.' + axis;

            var selectedInputs = this.getBlockElement().select(
                '.queryMultiple2DScalesQuestionContainer table input[name="'
                    + inputName + '"]:checked');
            if (selectedInputs.length) {
              return selectedInputs[0].value;
            }

            return null;
          },

          _isAllValuesSet : function() {
            for (var thesisIndex = 0, thesisCount = this._getThesisCount(); thesisIndex < thesisCount; thesisIndex++) {
              var optionX = this._getSelectedThesisOption(thesisIndex, 'x');
              var optionY = this._getSelectedThesisOption(thesisIndex, 'y');
              if (optionX === null || optionY === null) {
                return false;
              }
            }

            return true;
          },

          _updateNextButton : function() {
            if (!this._isAllValuesSet()) {
              this.getBlockController().disableNext();
            } else {
              this.getBlockController().enableNext();
            }
          },

          _onValueChange : function() {
            this._updateNextButton();
          }
        });

Scale2DQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);

    var questionContainers = this.getBlockElement().select(".queryQuestionContainer");
    if (questionContainers.length == 1) {
      var questionContainer = questionContainers[0];
      if (questionContainer.hasClassName("queryScaleGraphQuestionContainer")) {
        this._initializeGraph(questionContainer);
      }
    } else {
      if (questionContainers[0].hasClassName("queryScaleSliderQuestionContainer") && questionContainers[1].hasClassName("queryScaleSliderQuestionContainer")) {
        this._initializeSlider(questionContainers[0], questionContainers[1]);
      } else if (questionContainers[0].hasClassName("queryScaleRadioListQuestionContainer") && questionContainers[1].hasClassName("queryScaleRadioListQuestionContainer")) {
        this._initializeRadioList(questionContainers[0], questionContainers[1]);
      }
    }

    var liveReportContainer = this.getBlockElement().down('.queryLiveReportContainer');
    if (liveReportContainer) {
      this._liveReportController = new QueryBubbleChartLiveReportController("report_bubblechart", liveReportContainer, this._min1, this._max1, this._min2, this._max2);
      this._updateReport();
    }
  },

  deinitialize : function($super) {
    $super();
    if (this._sliderController1) {
      this._sliderController1.deinitialize();
    }

    if (this._sliderController2) {
      this._sliderController2.deinitialize();
    }
    
    if (this._radioListController1) {
      this._radioListController1.deinitialize();
    }
    
    if (this._radioListController2) {
      this._radioListController2.deinitialize();
    }
    
    if (this._graphController) {
      this._graphController.deinitialize();
    }
    
    if (this._liveReportController) {
      this._liveReportController.deinitialize();
    }
  },

  _initializeSlider : function(questionContainer1, questionContainer2) {
    this._sliderController1 = new QueryBlockScaleSliderFragmentController(questionContainer1);
    this._sliderController1.addListener("valueChange", this, this._onSlider1ValueChange);
    this._min1 = this._sliderController1.getMin();
    this._max1 = this._sliderController1.getMax();
    this._value1 = this._sliderController1.getSelected();
    this._sliderController2 = new QueryBlockScaleSliderFragmentController(questionContainer2);
    this._sliderController2.addListener("valueChange", this, this._onSlider2ValueChange);
    this._min2 = this._sliderController2.getMin();
    this._max2 = this._sliderController2.getMax();
    this._value2 = this._sliderController2.getSelected();
    this._updateNextButton();
  },

  _initializeRadioList : function(questionContainer1, questionContainer2) {
    this._radioListController1 = new QueryBlockScaleRadioListFragmentController(questionContainer1);
    this._radioListController1.addListener("valueChange", this, this._onRadioList1ValueChange);

    this._min1 = this._radioListController1.getMin();
    this._max1 = this._radioListController1.getMax();
    this._value1 = this._radioListController1.getSelected();

    this._radioListController2 = new QueryBlockScaleRadioListFragmentController(questionContainer2);
    this._radioListController2.addListener("valueChange", this, this._onRadioList2ValueChange);

    this._min2 = this._radioListController2.getMin();
    this._max2 = this._radioListController2.getMax();
    this._value2 = this._radioListController2.getSelected();
    this._updateNextButton();
  },

  _initializeGraph : function(questionContainer) {
    this._graphController = new QueryBlockScaleGraphFragmentController(questionContainer);
    this._graphController.addListener("valueChange", this, this._onGraphValueChange);

    this._min1 = this._graphController.getMinX();
    this._max1 = this._graphController.getMaxX();
    this._value1 = this._graphController.getSelectedX();
    this._min2 = this._graphController.getMinY();
    this._max2 = this._graphController.getMaxY();
    this._value2 = this._graphController.getSelectedY();
    this._updateNextButton();
  },

  _updateNextButton : function() {
    if (this._value1 === null || this._value2 === null) {
      this.getBlockController().disableNext();
    } else {
      this.getBlockController().enableNext();
    }
  },

  _updateReport : function() {
    if (this._liveReportController) {
      this._liveReportController.setUserValues([ [ this._value1, this._value2, 1 ] ]);
      this._liveReportController.draw();
    }
  },

  _onSlider1ValueChange : function(event) {
    this._value1 = event.value;
    this._updateNextButton();
    this._updateReport();
  },

  _onSlider2ValueChange : function(event) {
    this._value2 = event.value;
    this._updateNextButton();
    this._updateReport();
  },

  _onRadioList1ValueChange : function(event) {
    this._value1 = event.value;
    this._updateNextButton();
    this._updateReport();
  },

  _onRadioList2ValueChange : function(event) {
    this._value2 = event.value;
    this._updateNextButton();
    this._updateReport();
  },

  _onGraphValueChange : function(event) {
    this._value1 = event.valueX;
    this._value2 = event.valueY;
    this._updateNextButton();
    this._updateReport();
  }
});

ExpertiseQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);
    var questionContainer = this.getBlockElement().down(
        ".queryQuestionContainer");

    this._expretiseController = new QueryBlockExpertiseFragmentController(
        questionContainer);
    this._expretiseController.addListener("valueChange", this,
        this._onValueChange);
    this._userValues = this._indexesToUserValues(this._expretiseController
        .getSelectedIndexes());

    var liveReportContainer = this.getBlockElement().down(
        '.queryLiveReportContainer');
    if (liveReportContainer) {
      var expertiseCount = this._expretiseController.getExpertiseCount();
      var interestCount = this._expretiseController.getInterestCount();

      this._liveReportController = new QueryBubbleChartLiveReportController(
          "report_bubblechart", liveReportContainer, 0, expertiseCount - 1, 0,
          interestCount - 1);
      this._updateReport();
    }
    ;
  },
  deinitialize : function($super) {
    $super();

    if (this._expretiseController)
      this._expretiseController.deinitialize();
  },
  _updateReport : function() {
    if (this._liveReportController) {
      this._liveReportController.setUserValues(this._userValues);
      this._liveReportController.draw();
    }
  },
  _indexesToUserValues : function(selectedIndexes) {
    var userValues = new Array();
    for (var i = 0, l = selectedIndexes.length; i < l; i++) {
      var selectedIndex = selectedIndexes[i];
      userValues
          .push([
              selectedIndex.x,
              this._expretiseController.getInterestCount() - selectedIndex.y
                  - 1, 1 ]);
    }
    return userValues;
  },
  _onValueChange : function(event) {
    this._userValues = this._indexesToUserValues(event.selectedIndexes);
    this._updateReport();
  }
});

MultiSelectQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);

    var questionContainer = this.getBlockElement().down(
        ".queryQuestionContainer");
    this._multiSelectController = new QueryBlockMultiSelectFragmentController(
        questionContainer);

    var liveReportContainer = this.getBlockElement().down(
        '.queryLiveReportContainer');
    if (liveReportContainer) {
      this._multiSelectController.addListener("valueChange", this,
          this._onMultiSelectControllerValueChange);

      this._tickLabels = this._multiSelectController.getLabels();
      this._min = 0;
      this._max = this._tickLabels.length - 1;

      this._liveReportController = new QueryBarChartLiveReportController(
          "report_barchart", liveReportContainer, this._min, this._max,
          this._tickLabels);
      this._updateReport();
    }
  },
  deinitialize : function($super) {
    $super();
  },
  _updateReport : function() {
    if (this._liveReportController) {

      var selected = this._multiSelectController.getSelectedIndexes();
      for (var i = this._min, l = this._max + 1; i < l; i++) {
        this._liveReportController.setUserData(i, selected.indexOf(i) > -1 ? 1
            : 0);
      }

      this._liveReportController.draw();
    }
  },
  _onMultiSelectControllerValueChange : function(event) {
    this._updateReport();
  }
});

OrderQueryPageController = Class
    .create(
        QueryPageController,
        {
          initialize : function($super, blockController) {
            $super(blockController);
            var questionContainer = this.getBlockElement().down(
                ".queryQuestionContainer");

            this._orderingController = new QueryBlockOrderingFragmentController(
                questionContainer);

            var liveReportContainer = this.getBlockElement().down(
                '.queryLiveReportContainer');
            if (liveReportContainer) {
              this._orderingController.addListener("valueChange", this,
                  this._onValueChange);

              this._liveReportController = new QueryStackedBarChartLiveReportController(
                  "report_stackedbarchart", liveReportContainer);
              this._updateChart();
            }
          },
          deinitialize : function($super) {
            $super();

            this._orderingController.deinitialize();
          },
          _updateChart : function() {
            if (this._liveReportController) {
              var order = this._orderingController.getOrder();
              var userData = new Array();
              for (var i = 0, l = order.length; i < l; i++) {
                var index = order.indexOf(i);
                var row = new Array();

                for (var j = 0; j < l; j++) {
                  if (j == index) {
                    row.push(1.0);
                  } else {
                    row.push(0.0);
                  }
                }

                userData.push(row);
              }

              this._liveReportController.setUserData(userData);
              this._liveReportController.draw();
            }
          },
          _onValueChange : function(event) {
            this._updateChart();
          }
        });

GroupingQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);

    var questionContainer = this.getBlockElement().down(
        ".queryQuestionContainer");

    this._groupingController = new QueryBlockGroupingFragmentController(
        questionContainer);

    var liveReportContainer = this.getBlockElement().down(
        '.queryLiveReportContainer');
    if (liveReportContainer) {
      this._groupingController.addListener("valueChange", this,
          this._onGroupingControllerValueChange);

      this._reportControllers = new Hash();
      var _this = this;

      var getElementValue = function(element, name) {
        var inputElement = element.down('input[name="' + name + '"]');
        var value = inputElement.value;
        inputElement.remove();
        return value;
      };

      liveReportContainer.select('.queryPieChart').each(
          function(element) {
            var id = getElementValue(element, 'chart.id');
            var caption = getElementValue(element, 'chart.caption');
            var ids = new Array();
            var values = new Array();
            var captions = new Array();

            var dataSetSize = parseInt(getElementValue(element,
                'chart.dataSetSize'));

            for (var i = 0, l = dataSetSize; i < l; i++) {
              var itemId = getElementValue(element, 'chart.dataSet.' + i
                  + '.id');
              var itemCaption = getElementValue(element, 'chart.dataSet.' + i
                  + '.caption');
              var itemValue = getElementValue(element, 'chart.dataSet.' + i
                  + '.value');

              ids.push(itemId);
              values.push(itemValue);
              captions.push(itemCaption);
            }

            var controller = new QueryPieChartLiveReportController(
                "report_piechart", element, caption, ids, values, captions);
            _this._reportControllers.set(id, controller);
          });

      this._setUserValue(this._groupingController.getValueSet());
    }
  },
  deinitialize : function($super) {
    $super();

    this._groupingController.deinitialize();
  },
  _updateCharts : function() {
    if (this._reportControllers) {
      this._reportControllers.each(function(controller) {
        controller.value.draw();
      });
    }
  },
  _setUserValue : function(valueSet) {
    var keys = valueSet.keys();
    for (var i = 0, l = keys.length; i < l; i++) {
      var key = keys[i];
      var value = valueSet.get(key);

      var reportController = this._reportControllers.get(key);
      reportController.setUserData(value);
    }

    this._updateCharts();
  },
  _onGroupingControllerValueChange : function(event) {
    this._setUserValue(event.valueSet);
  }
});

TimeLineQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);
    var questionContainer = this.getBlockElement().down(
        ".queryQuestionContainer");

    this._timeLineController = new QueryBlockTimelineFragmentController(
        questionContainer);
    this._timeLineController.addListener("valueChange", this,
        this._onTimeLineValueChange);

    /**
     * timeline.type: 0: 1 value 1: 2 values
     */
    this._value1 = this._timeLineController.getValue1();
    if (this._timeLineController.getType() == 1)
      this._value2 = this._timeLineController.getValue2();
    this._min = this._timeLineController.getMin();
    this._max = this._timeLineController.getMax();
    this._step = this._timeLineController.getStep();
    this._tickLabels = new Array();

    var liveReportContainer = this.getBlockElement().down(
        '.queryLiveReportContainer');
    if (liveReportContainer) {
      if (this._timeLineController.getType() == 1) {
        this._liveReportController = new QueryBubbleChartLiveReportController(
            "report_bubblechart", liveReportContainer, this._min, this._max,
            this._min, this._max);
        this._updateBubbleChartReport();
      } else {
        for (var i = this._min; i <= this._max; i += this._step) {
          this._tickLabels.push(i);
        }
        this._liveReportController = new QueryBarChartLiveReportController(
            "report_barchart", liveReportContainer, 0,
            this._tickLabels.size() - 1, this._tickLabels);
        this._updateBarChartReport();
      }
    }
  },
  deinitialize : function($super) {
    $super();

    this._timeLineController.deinitialize();

    if (this._liveReportController) {
      this._liveReportController.deinitialize();
    }
  },
  _updateBubbleChartReport : function() {
    if (this._liveReportController) {
      this._liveReportController.setUserValues([ [ this._value1, this._value2,
          1 ] ]);

      this._liveReportController.draw();
    }
  },
  _updateBarChartReport : function() {
    if (this._liveReportController) {
      var value = parseInt(this._value1);
      for (var i = 0; i < this._tickLabels.size(); i++) {
        this._liveReportController.setUserData(i,
            value == this._tickLabels[i] ? 1 : 0);
      }
      this._liveReportController.draw();
    }
  },
  _onTimeLineValueChange : function(event) {
    this._value1 = event.value1;
    this._value2 = event.value2;
    if (event.type == 1) {
      this._updateBubbleChartReport();
    } else {
      this._updateBarChartReport();
    }
  }
});

FormQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);

    this._sliders = new Array();

    var _this = this;
    this.getBlockElement().select('.queryFormFieldContainer').each(
        function(fieldContainer) {
          _this._initializeFieldContainer(fieldContainer);
        });
  },
  deinitialize : function($super) {
    $super();
    for (var i = 0, l = this._sliders.length; i < l; i++) {
      this._sliders[i].element.purge();
      this._sliders[i].destroy();
    }
  },
  _initializeFieldContainer : function(fieldContainer) {
    var sliderTrack = fieldContainer.down(".queryFormSliderTrack");
    if (sliderTrack) {
      this._initializeSlider(fieldContainer, sliderTrack);
    }
  },
  _initializeSlider : function(fieldContainer, sliderTrack) {
    var possibleValueElements = fieldContainer
        .select('input[name="possibleValues"]');
    var possibleValues = possibleValueElements.pluck('value');
    possibleValueElements.invoke("remove");
    var value = fieldContainer.down('.sliderValue').value;

    var slider = new S2.UI.Slider(sliderTrack, {
      value : {
        initial : value || 1,
        min : 1,
        max : possibleValues.length
      },
      possibleValues : possibleValues
    });

    Event.observe(slider.element, "ui:slider:value:changed", function(event) {
      fieldContainer.down('.sliderValue').value = event.memo.values[0] || '';
    });

    var labels = fieldContainer.select('.queryFormSliderItemLabel');

    var labelStep = 100 / (possibleValues.length - 1);

    for (var i = 0, l = labels.length; i < l; i++) {
      var label = labels[i];

      if (i == 0) {
        // First label is always in far left
        label.setStyle({
          left : '0px'
        });
      } else if (i == (labels.length - 1)) {
        // ...and last in far right
        label.setStyle({
          right : '0px'
        });
      } else {
        // others are positioned by percentage
        var labelWidth = label.getLayout().get("width");
        label.setStyle({
          left : (labelStep * i) + '%',
          marginLeft : (-labelWidth / 2) + 'px'
        });
      }

    }
    ;
  }
});

Collage2DQueryPageController = Class.create(QueryPageController, {
  initialize : function($super, blockController) {
    $super(blockController);

    this._collage2dFragmentController = new Collage2DFragmentController(this
        .getBlockElement().down(".queryCollage2DQuestionFlotrContainer"));
  },
  deinitialize : function($super) {
    $super();
  }
});

QueryBlockFragmentController = Class.create({
  initialize : function(name, element) {
    this._name = name;
    this._element = element;
  },
  deinitialize : function() {
  },
  getElement : function() {
    return this._element;
  },
  getJsDataVariable : function(name) {
    return JSDATA["queryFragment." + this._name + "." + name];
  }
});

Object.extend(QueryBlockFragmentController.prototype,
    fni.events.FNIEventSupport);

Collage2DFragmentController = Class
    .create(
        QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('collage2d', element);

            this._minX = Infinity;
            this._maxX = -Infinity;
            this._minY = Infinity;
            this._maxY = -Infinity;

            var xTicks = new Array();
            var xCount = parseInt(this.getJsDataVariable("options.x.count"));
            for (var i = 0; i < xCount; i++) {
              var value = parseInt(this.getJsDataVariable("options.x." + i
                  + ".value"));
              var text = this.getJsDataVariable("options.x." + i + ".text");
              xTicks.push([ value, text ]);
              this._minX = Math.min(this._minX, value);
              this._maxX = Math.max(this._maxX, value);
            }

            var yTicks = new Array();
            var yCount = parseInt(this.getJsDataVariable("options.y.count"));
            for (var i = 0; i < yCount; i++) {
              var value = parseInt(this.getJsDataVariable("options.y." + i
                  + ".value"));
              var text = this.getJsDataVariable("options.y." + i + ".text");
              yTicks.push([ value, text ]);
              this._minY = Math.min(this._minY, value);
              this._maxY = Math.max(this._maxY, value);
            }

            this._xAxisTitle = this.getJsDataVariable("options.x.label");
            this._yAxisTitle = this.getJsDataVariable("options.y.label");

            var labelVisibility = this.getJsDataVariable("labelVisibility");
            var dotOffset = this.getJsDataVariable("dotOffset");
            var dimensions = this.getElement().getDimensions();
            var aspect = dimensions.width / dimensions.height;
            var offsetPct = (dotOffset || 1) / 100;

            var diffX = xCount * (offsetPct * 2);
            var diffY = xCount * (offsetPct * 2 * aspect);

            this._flotrOptions = {
              "xaxis" : {
                min : this._minX - diffX,
                max : this._maxX + diffX,
                title : this._xAxisTitle,
                ticks : xTicks
              },
              "yaxis" : {
                min : this._minY - diffY,
                max : this._maxY + diffY,
                title : this._yAxisTitle,
                ticks : yTicks
              },
              "bubbles" : {
                "show" : true
              },
              "valuelabels" : {
                "show" : labelVisibility == 'ALWAYS',
                labelFormatter : function(obj) {
                  return obj.series.label;
                },
                position : function(obj) {
                  var offsetAngle = obj.series.angles
                      && obj.series.angles[[ obj.x, obj.y ].join('-')];
                  if (offsetAngle) {
                    if (offsetAngle <= (Math.PI / 2)) {
                      return 'se';
                    } else if (offsetAngle <= (Math.PI)) {
                      return 'ne';
                    } else if (offsetAngle <= ((Math.PI / 2) * 3)) {
                      return 'nw';
                    } else {
                      return 'sw';
                    }
                  }

                  return 'se';
                }
              },
              points : {
                show : true
              },
              mouse : {
                track : labelVisibility == 'HOVER',
                position : 'se',
                relative : true,
                trackFormatter : function(obj) {
                  return obj.series.label;
                },
                margin : 5,
                lineColor : '#aaa',
                trackDecimals : 0,
                sensibility : 20,
                trackY : true,
                radius : 2
              }
            };

            this._dataSets = this._adjustDataSets(xCount, yCount, offsetPct,
                aspect, this.getJsDataVariable("dataSets").evalJSON());

            this._renderFlotr();
          },
          deinitialize : function($super) {
            $super();
          },

          _adjustDataSets : function(xCount, yCount, dotOffset, aspect,
              dataSets) {
            var hash = new Hash();

            var datas = dataSets.pluck('data');

            var flatten = new Array();
            for (var i = 0, l = datas.length; i < l; i++) {
              for (var j = 0, jl = datas[i].length; j < jl; j++) {
                flatten.push([ datas[i][j][0], datas[i][j][1] ].join('-'));
              }
            }

            while (flatten.length > 0) {
              var value = flatten.pop();
              var count = 0;
              var index = null;
              while ((index = flatten.indexOf(value)) != -1) {
                count++;
                flatten.splice(index, 1);
              }

              if (count > 0) {
                hash.set(value, count);
              }
            }

            var overlapKeys = hash.keys();
            for (var overlapKeyIndex = 0, overlapKeysLenght = overlapKeys.length; overlapKeyIndex < overlapKeysLenght; overlapKeyIndex++) {
              var overlapKey = overlapKeys[overlapKeyIndex];
              var count = hash.get(overlapKey) + 1;
              var orbitAngle = (2 * Math.PI) / count;
              var orbitRadiusX = xCount * dotOffset;
              var orbitRadiusY = yCount * dotOffset * aspect;
              var index = 0;
              var xy = overlapKey.split('-');
              xy[0] = parseInt(xy[0]);
              xy[1] = parseInt(xy[1]);

              for (var i = 0, l = dataSets.length; i < l; i++) {
                for (var j = 0, jl = dataSets[i].data.length; j < jl; j++) {
                  if ((dataSets[i].data[j][0] == xy[0])
                      && (dataSets[i].data[j][1] == xy[1])) {
                    var angle = (orbitAngle * index) + (Math.PI / 4);
                    dataSets[i].data[j][0] = dataSets[i].data[j][0]
                        - Math.sin(angle) * -orbitRadiusX;
                    dataSets[i].data[j][1] = dataSets[i].data[j][1]
                        + Math.cos(angle) * -orbitRadiusY;

                    if (!dataSets[i].angles) {
                      dataSets[i].angles = new Object();
                    }

                    dataSets[i].angles[[ dataSets[i].data[j][0],
                        dataSets[i].data[j][1] ].join('-')] = angle;
                    index++;
                  }
                  ;
                }
              }
            }

            return dataSets;
          },

          _renderFlotr : function() {
            this._flotr = Flotr.draw(this.getElement(), this._dataSets,
                this._flotrOptions);
          }
        });

QueryBlockScaleSliderFragmentController = Class.create(
    QueryBlockFragmentController, {
      initialize : function($super, element) {
        $super('scale_slider', element);

        this._sliderMouseUpListener = this._onSliderMouseUp
            .bindAsEventListener(this);
        this._sliderValueChangedListener = this._onSliderValueChanged
            .bindAsEventListener(this);

        this._track = element.down('.queryScaleSliderTrack');
        this._input = element.down('input.sliderValue');
        this._possibleValues = [];
        this._valueLabels = [];
        this._minValue = 0;
        this._maxValue = 0;

        var trackDimensions = this._track.getDimensions();
        var labels = element.select('.queryScaleSliderItemLabel');
        var labelStep = trackDimensions.width / (labels.length - 1);

        for (var i = 0, l = labels.length; i < l; i++) {
          this._valueLabels.push(labels[i].innerHTML);
          var labelWidth = labels[i].getDimensions().width;
          var left = (labelStep * i) - (labelWidth / 2);
          labels[i].setStyle({
            left : left + 'px',
          });
        }

        var _this = this;
        element.select('input[name="possibleValues"]').each(function(element) {
          _this._minValue = Math.min(_this._minValue, element.value);
          _this._maxValue = Math.max(_this._maxValue, element.value);
          _this._possibleValues.push(element.value);
          element.remove();
        });

        var selectedValue = this._parseSelectedValue();
        if (selectedValue == null) {
          this._selectedValue = null;
          selectedValue = this._getDefaultValue();
        } else {
          this._selectedValue = selectedValue;
        }

        this._sliderHandle = this
            ._createHandle(this._valueLabels[selectedValue]);

        this._slider = new S2.UI.Slider(this._track, {
          value : {
            initial : selectedValue,
            min : this._minValue,
            max : this._maxValue
          },
          possibleValues : this._possibleValues,
          handles : [ this._sliderHandle ]
        });

        Event.observe(this._slider.element, "mouseup",
            this._sliderMouseUpListener);
        Event.observe(this._slider.element, "ui:slider:value:changed",
            this._sliderValueChangedListener);
      },

      deinitialize : function($super) {
        Event.stopObserving(this._slider.element, "mouseup",
            this._sliderMouseUpListener);
        Event.stopObserving(this._slider.element, "ui:slider:value:changed",
            this._sliderValueChangedListener);
        this._slider.destroy();
        $super();
      },

      getMin : function() {
        return this._minValue;
      },

      getMax : function() {
        return this._maxValue;
      },

      getPossibleValues : function() {
        return this._possibleValues;
      },

      getValueLabels : function() {
        return this._valueLabels;
      },

      getSelected : function() {
        return this._selectedValue;
      },

      _getDefaultValue : function() {
        return Math.floor(this._possibleValues.length / 2);
      },

      _parseSelectedValue : function() {
        var result = parseFloat(this._input.value);
        if (isNaN(result)) {
          return null;
        }

        return result;
      },

      _createHandle : function(label) {
        var handle = new Element("a", {
          href : "#",
          className : "queryScaleHandle",
          title : label
        });

        var labelContainer = new Element("span", {
          className : "queryScaleHandleLabelContainer"
        });

        labelContainer.appendChild(new Element("span", {
          className : "queryScaleHandleLabel"
        }).update(label));

        handle.appendChild(labelContainer);

        return handle;
      },

      _handleValueChange : function(value) {
        this._input.value = value;
        this._selectedValue = value;

        this._sliderHandle.down('.queryScaleHandleLabel').update(
            this._valueLabels[this._selectedValue]);
        this._sliderHandle.title = this._valueLabels[this._selectedValue];

        this.fire("valueChange", {
          value : this._selectedValue
        });
      },

      _onSliderValueChanged : function(event) {
        this._handleValueChange(event.memo.values[0]);
      },

      _onSliderMouseUp : function() {
        if (this._parseSelectedValue() === null) {
          this._handleValueChange(this._getDefaultValue());
        }
      }
    });

QueryBlockScaleRadioListFragmentController = Class.create(QueryBlockFragmentController, {
  initialize : function($super, element) {
    $super('scale_radiolist', element);

    this._itemValueChangeListener = this._onItemValueChange.bindAsEventListener(this);

    this._minValue = Infinity;
    this._maxValue = -Infinity;
    this._selectedValue = null;
    this._valueLabels = new Array();

    var listItems = element.select('.queryScaleRadioListItemInput');
    for (var i = 0, l = listItems.length; i < l; i++) {
      var listItem = listItems[i];

      this._minValue = Math.min(parseInt(listItem.value), this._minValue);
      this._maxValue = Math.max(parseInt(listItem.value), this._maxValue);
      var itemValue = listItem.value;
      var itemContainer = listItems[i].up('.queryScaleRadioListItemContainer');
      if (itemContainer != undefined) {
        var labelContainer = itemContainer.down('.queryScaleRadioListItemLabel');
        if (labelContainer != undefined) {
          itemValue = labelContainer.innerHTML;
        }
      }
      
      this._valueLabels.push(itemValue);
      if (listItem.checked) {
        this._selectedValue = listItem.value;
      }
          
      Event.observe(listItem, "change", this._itemValueChangeListener);
    }
  },
  
  deinitialize : function($super) {
    this.getElement().select('.queryScaleRadioListItemInput').each(function(e) {
      e.purge();
    });

    $super();
  },
  
  getMin : function() {
    return this._minValue;
  },
  
  getMax : function() {
    return this._maxValue;
  },
  
  getValueLabels : function() {
    return this._valueLabels;
  },
  
  getSelected : function() {
    return this._selectedValue;
  },
  
  _onItemValueChange : function(event) {
    var element = Event.element(event);
    this._selectedValue = element.value;
    this.fire("valueChange", {
      value : element.value
    });
  }
});

QueryBlockTimelineFragmentController = Class
    .create(
        QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('timeline', element);

            this._sliderValueChangedListener = this._onSliderValueChanged
                .bindAsEventListener(this);

            this._track = element.down('.queryTimelineTrack');
            this._value1Element = element.down('input[name="value1"]');
            this._value2Element = element.down('input[name="value2"]');
            this._typeElement = element.down('input[name="type"]');

            this._min = parseInt(element.down('.queryTimelineMin').innerHTML);
            this._max = parseInt(element.down('.queryTimelineMax').innerHTML);
            this._step = parseInt(this.getJsDataVariable("step"));
            this._type = parseInt(this._typeElement.value);

            var possibleValues = new Array();
            for (var i = this._min; i <= this._max; i += this._step) {
              possibleValues.push(i);
            }

            var handles = new Array();
            var values = new Array();
            values
                .push(this._value1Element.value ? parseInt(this._value1Element.value)
                    : this._min);

            this._label1 = this.getElement().down(
                'label.queryTimelineValue1Label').innerHTML;

            handles.push(this._createHandle("queryTimelineFirstHandle",
                this._label1));

            if (this._type == 1) {
              this._label2 = element.down('label.queryTimelineValue2Label').innerHTML;
              values
                  .push(this._value2Element.value ? parseInt(this._value2Element.value)
                      : this._min);
              handles.push(this._createHandle("queryTimelineSecondHandle",
                  this._label2));
            }

            this._slider = new S2.UI.Slider(this._track, {
              value : {
                initial : values,
                min : this._min,
                max : this._max
              },
              possibleValues : possibleValues,
              handles : handles
            });

            Event.observe(this._slider.element, "ui:slider:value:changed",
                this._sliderValueChangedListener);

            this._updateHandleLabels();
          },
          deinitialize : function($super) {
            Event.stopObserving(this._slider.element,
                "ui:slider:value:changed", this._sliderValueChangedListener);
            this._slider.destroy();
            $super();
          },
          getType : function() {
            return this._type;
          },
          getMin : function() {
            return this._min;
          },
          getMax : function() {
            return this._max;
          },
          getStep : function() {
            return this._step;
          },
          getValue1 : function() {
            return parseInt(this._value1Element.value);
          },
          getValue2 : function() {
            return parseInt(this._value2Element.value);
          },
          _createHandle : function(className, label) {
            var handle = new Element("a", {
              href : "#",
              className : className,
              title : label
            });

            var labelContainer = new Element("span", {
              className : "queryTimelineHandleLabelContainer"
            });

            labelContainer.appendChild(new Element("span", {
              className : "queryTimelineHandleLabel"
            }).update(label));

            handle.appendChild(labelContainer);

            return handle;
          },
          _updateHandleLabels : function() {
            var handles = this._slider.handles;

            var label1 = this._label1 + ' - ' + this._value1Element.value;

            handles[0].down('.queryTimelineHandleLabel').update(label1);

            if (this.getType() == 1) {
              var label2 = this.getElement().down(
                  'label.queryTimelineValue2Label').innerHTML
                  + ' - ' + this._value2Element.value;
              handles[1].down('.queryTimelineHandleLabel').update(label2);
            }
          },
          _onSliderValueChanged : function(event) {
            this._value1Element.value = event.memo.values[0];
            if (this._type == 1) {
              this._value2Element.value = event.memo.values[1];

              this.fire("valueChange", {
                value1 : parseInt(this._value1Element.value),
                value2 : parseInt(this._value2Element.value),
                type : this._type
              });

            } else {
              this.fire("valueChange", {
                value1 : parseInt(this._value1Element.value),
                type : this._type
              });
            }

            this._updateHandleLabels();
          }
        });

QueryBlockExpertiseFragmentController = Class
    .create(QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('expertise', element);

            this._cellClickListener = this._onCellClick
                .bindAsEventListener(this);

            var _this = this;
            this.getElement().select('.queryExpertiseMatrixAnswerCell').each(
                function(cell) {
                  if (!cell.hasClassName("queryExpertiseMatrixHeaderCell")) {
                    Event.observe(cell, "click", _this._cellClickListener);
                  }
                });
          },
          deinitialize : function($super) {
            $super();

            this.getElement().select('.queryExpertiseMatrixAnswerCell').invoke(
                'purge');
          },
          getInterestCount : function() {
            return parseInt(this.getElement().down(
                'input[name="interestCount"]').value);
          },
          getExpertiseCount : function() {
            return parseInt(this.getElement().down(
                'input[name="expertiseCount"]').value);
          },
          getSelectedIndexes : function() {
            var inputs = this.getElement().select(
                '.queryExpertiseMatrixAnswerCell input[type="hidden"]');

            var result = new Array();

            for (var i = 0, l = inputs.length; i < l; i++) {
              var input = inputs[i];
              if (input.value == "1") {
                var colElement = input.up('.queryExpertiseMatrixAnswerCell');
                var rowElement = colElement.up('.queryExpertiseMatrixRow');
                var matrixElement = rowElement.up('.queryExpertiseMatrix');

                var x = rowElement.select('.queryExpertiseMatrixAnswerCell')
                    .indexOf(colElement);
                var y = matrixElement.select('.queryExpertiseMatrixRow')
                    .indexOf(rowElement);

                result.push({
                  x : x,
                  y : y
                });
              }
            }

            return result;
          },
          _onCellClick : function(event) {
            var cellElement = Event.element(event);
            if (cellElement.hasClassName("queryExpertiseMatrixCellSelected")) {
              cellElement.removeClassName("queryExpertiseMatrixCellSelected");
              cellElement.down('input[type="hidden"]').value = '0';
            } else {
              cellElement.addClassName("queryExpertiseMatrixCellSelected");
              cellElement.down('input[type="hidden"]').value = '1';
            }

            this.fire("valueChange", {
              selectedIndexes : this.getSelectedIndexes()
            });
          }
        });

QueryBlockTimeSerieFragmentController = Class
    .create(
        QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('time_serie', element);

            this._flotrContainerClickListener = this._onFlotrContainerClick
                .bindAsEventListener(this);
            this._flotrContainerMouseMoveListener = this._onFlotrContainerMouseMove
                .bindAsEventListener(this);

            this._maxY = parseFloat(this.getJsDataVariable("maxY"));
            this._maxX = parseFloat(this.getJsDataVariable("maxX"));
            this._minY = parseFloat(this.getJsDataVariable("minY"));
            this._minX = parseFloat(this.getJsDataVariable("minX"));
            this._stepX = parseFloat(this.getJsDataVariable("stepX") || 1);
            this._stepY = parseFloat(this.getJsDataVariable("stepY") || 1);
            this._userStepX = parseFloat(this.getJsDataVariable("userStepX")
                || this._stepX);
            this._xAxisTitle = this.getJsDataVariable("xAxisTitle");
            this._yAxisTitle = this.getJsDataVariable("yAxisTitle");
            this._predefinedSetLabel = this
                .getJsDataVariable("predefinedSetLabel");
            this._userSetLabel = this.getJsDataVariable("userSetLabel");

            this._yTickDecimals = parseFloat(this
                .getJsDataVariable("yTickDecimals"));
            if (isNaN(this._yTickDecimals))
              this._yTickDecimals = null;

            this._columnInputs = element.up('.queryBlock').select(
                'input.queryTimeSerieQuestionInput');

            var yTicks = Math.round((this._maxY - this._minY) / this._stepY);
            var xTicks = Math.round((this._maxX - this._minX)
                / Math.getGCD(this._stepX, this._userStepX));

            var diffX = (this._maxX - this._minX) * 0.02;

            this._flotrOptions = {
              "xaxis" : {
                min : this._minX - diffX,
                max : this._maxX + diffX,
                tickDecimals : 0,
                noTicks : xTicks,
                title : this._xAxisTitle
              },
              "yaxis" : {
                min : this._minY,
                max : this._maxY,
                tickDecimals : this._yTickDecimals,
                noTicks : yTicks,
                title : this._yAxisTitle
              },
              "legend" : {
                "noColumns" : 1,
                "position" : "ne"
              }
            };

            this._flotrContainer = element
                .down('.queryTimeSerieQuestionFlotrContainer');
            // TODO: ???
            this.getElement().appendChild(this._flotrContainer);
            this._dataSerie = new Array();
            this._userDataSerie = new Array();

            var predefinedValuesCount = this
                .getJsDataVariable("predefinedValues.count");
            for (var i = 0; i < predefinedValuesCount; i++) {
              var y = parseFloat(this.getJsDataVariable("predefinedValues." + i
                  + ".y"));
              if (!isNaN(y))
                this._dataSerie.push([
                    parseFloat(this.getJsDataVariable("predefinedValues." + i
                        + ".x")), y ]);
            }

            this._dataSerie = this._dataSerie.sort(function(a, b) {
              return a[0] - b[0];
            });

            var x;
            var y;

            if (this._dataSerie.length > 0) {
              var lastPredefined = this._dataSerie[this._dataSerie.length - 1];
              x = lastPredefined[0];
              y = lastPredefined[1];
            } else {
              x = this._minX;
              y = this._minY;
            }

            var i = this._dataSerie.length > 0 ? -1 : 0;

            while (x <= this._maxX) {
              var value = i >= 0 ? parseFloat(this._columnInputs[i].value) : y;
              if (isNaN(value))
                value = y;

              this._userDataSerie.push([ x, value ]);

              x += this._userStepX;
              if (x > this._maxX) {
                // this._userDataSerie.push([ this._maxX, value ]);
                break;
              }

              i++;
            }

            var l = Math.ceil((this._maxX - x) / this._stepX);
            for (i = 0; i < l; i++) {
              this._columnInputs[i].value = this._userDataSerie[i][1];
            }

            this._userDataSerie = this._userDataSerie.sort(function(a, b) {
              return a[0] - b[0];
            });

            Event.observe(this._flotrContainer, 'flotr:click',
                this._flotrContainerClickListener);
            Event.observe(this._flotrContainer, 'flotr:mousemove',
                this._flotrContainerMouseMoveListener);

            this._previousHighlightedDot = null;
            this._highlightRadius = 3;
            this._highlightLineWidth = 1;
            this._highlightColor = "#000000";
            this._highlightFill = null;

            this._renderFlotr();
          },
          deinitialize : function($super) {
            $super();

            Event.stopObserving(this._flotrContainer, 'flotr:click',
                this._flotrContainerClickListener);
            Event.stopObserving(this._flotrContainer, 'flotr:mousemove',
                this._flotrContainerMouseMoveListener);
          },
          getMaxY : function() {
            return this._maxY;
          },
          getMaxX : function() {
            return this._maxX;
          },
          getMinY : function() {
            return this._minY;
          },
          getMinX : function() {
            return this._minX;
          },
          getStepX : function() {
            return this._stepX;
          },
          getUserStepX : function() {
            return this._userStepX;
          },
          getStepY : function() {
            return this._stepY;
          },
          getUserDataSerie : function() {
            return this._userDataSerie;
          },

          getPredefinedValueCount : function() {
            return this._dataSerie.length;
          },

          hasPredefinedValues : function() {
            return this.getPredefinedValueCount() > 0;
          },

          _renderFlotr : function() {
            this._flotr = Flotr.draw(this._flotrContainer, [ {
              data : this._dataSerie,
              label : this._predefinedSetLabel,
              lines : {
                show : true,
                fill : false
              },
              points : {
                "show" : true,
                "radius" : 3,
                "lineWidth" : 2,
                "fill" : true,
                "fillColor" : "#FFFFFF",
                "fillOpacity" : 0.4
              }
            }, {
              data : this._userDataSerie,
              label : this._userSetLabel,
              lines : {
                show : true,
                fill : false
              },
              points : {
                "show" : true,
                "radius" : 3,
                "lineWidth" : 2,
                "fill" : true,
                "fillColor" : "#FFFFFF",
                "fillOpacity" : 0.4
              }
            } ], this._flotrOptions);
          },

          _getNearestDotIndex : function(position) {
            var firstIndex = this.hasPredefinedValues() ? 1 : 0;
            var valueCount = this._userDataSerie.length;
            var firstValue = this._userDataSerie.length > firstIndex ? this._userDataSerie[firstIndex][0]
                : null;
            var lastValue = this._userDataSerie[valueCount - 1][0];

            if (firstValue === null) {
              return null;
            }

            if (position.x < firstValue) {
              return firstIndex;
            } else if (position.x >= lastValue) {
              return valueCount - 1;
            } else {
              return this._findNearestDotIndex(position);
            }
          },

          _findNearestDotIndex : function(position) {
            var valueCount = this._userDataSerie.length;

            for (var i = 0; i < valueCount - 1; i++) {
              var x1 = this._userDataSerie[i][0];
              var x2 = this._userDataSerie[i + 1][0];

              if ((position.x >= x1) && (position.x <= x2)) {
                if ((position.x - x1) < (x2 - position.x)) {
                  return i;
                } else {
                  return i + 1;
                }
              }
            }

            return null;
          },

          _onFlotrContainerClick : function(event) {
            var position = event.memo[0];

            var dotIndex = this._getNearestDotIndex(position);
            if (dotIndex !== null) {
              this._userDataSerie[dotIndex][1] = position.y;
              this._columnInputs[dotIndex
                  - (this.hasPredefinedValues() ? 1 : 0)].value = position.y;
            }

            this._renderFlotr();

            this.fire("valueChange", {
              dataSerie : this._userDataSerie
            });
          },
          _onFlotrContainerMouseMove : function(event) {
            var position = event.memo[1];

            var dotIndex = this._getNearestDotIndex(position);
            if (dotIndex !== null) {
              var dotX = this._userDataSerie[dotIndex][0];
              var dotY = this._userDataSerie[dotIndex][1];

              var series = this._flotr.series;
              var xa = series[1].xaxis;
              var ya = series[1].yaxis;

              this._flotr.octx.save();
              this._flotr.octx.translate(this._flotr.plotOffset.left,
                  this._flotr.plotOffset.top);

              if (this._previousHighlightedDot) {
                this._flotr.octx.clearRect(this._previousHighlightedDot.x
                    - (this._highlightRadius + this._highlightLineWidth),
                    this._previousHighlightedDot.y
                        - (this._highlightRadius + this._highlightLineWidth),
                    (this._highlightRadius + this._highlightLineWidth) * 2,
                    (this._highlightRadius + this._highlightLineWidth) * 2);

                this._previousHighlightedDot = null;
              }

              var hitX = xa.d2p(dotX);
              var hitY = ya.d2p(dotY);

              this._flotr.octx.beginPath();
              this._flotr.octx.strokeStyle = this._highlightColor;
              this._flotr.octx.fillStyle = this._highlightFill;
              this._flotr.octx.arc(hitX, hitY, this._highlightRadius, 0,
                  2 * Math.PI);
              if (this._highlightFill)
                this._flotr.octx.fill();
              this._flotr.octx.stroke();
              this._flotr.octx.closePath();
              this._flotr.octx.restore();

              this._previousHighlightedDot = {
                x : hitX,
                y : hitY
              };
            }
          }
        });

QueryBlockScaleGraphFragmentController = Class
    .create(
        QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('scale_graph', element);

            this._flotrContainerClickListener = this._onFlotrContainerClick
                .bindAsEventListener(this);
            this._flotrContainerMouseMoveListener = this._onFlotrContainerMouseMove
                .bindAsEventListener(this);

            this._xValueInput = element.down('input[name="valueX"]');
            this._yValueInput = element.down('input[name="valueY"]');

            this._minX = Infinity;
            this._maxX = -Infinity;
            this._minY = Infinity;
            this._maxY = -Infinity;

            var xTicks = [];
            var optionsCount = parseInt(this
                .getJsDataVariable("options.x.count"));
            for (var i = 0; i < optionsCount; i++) {
              var value = parseInt(this.getJsDataVariable("options.x." + i
                  + ".value"));
              var text = this.getJsDataVariable("options.x." + i + ".text");
              xTicks.push([ value, text ]);
              this._minX = Math.min(this._minX, value);
              this._maxX = Math.max(this._maxX, value);
            }

            var yTicks = [];
            var optionsCount = parseInt(this
                .getJsDataVariable("options.y.count"));
            for (var i = 0; i < optionsCount; i++) {
              var value = parseInt(this.getJsDataVariable("options.y." + i
                  + ".value"));
              var text = this.getJsDataVariable("options.y." + i + ".text");
              yTicks.push([ value, text ]);
              this._minY = Math.min(this._minY, value);
              this._maxY = Math.max(this._maxY, value);
            }

            this._xAxisTitle = this.getJsDataVariable("options.x.label");
            this._yAxisTitle = this.getJsDataVariable("options.y.label");

            this._flotrOptions = {
              "xaxis" : {
                min : this._minX,
                max : this._maxX,
                title : this._xAxisTitle,
                ticks : xTicks
              },
              "yaxis" : {
                min : this._minY,
                max : this._maxY,
                title : this._yAxisTitle,
                ticks : yTicks
              }
            };

            this._flotrContainer = element
                .down('.queryScaleGraphQuestionFlotrContainer');
            this._dataSerie = [];
            var valueX = this._parseSelectedX();
            var valueY = this._parseSelectedY();

            if (valueX !== null && valueY !== null) {
              this._dataSerie.push([ valueX, valueY ]);
            } else {
              this._dataSerie
                  .push([ this._getDefaultX(), this._getDefaultY() ]);
            }

            Event.observe(this._flotrContainer, 'flotr:click',
                this._flotrContainerClickListener);
            Event.observe(this._flotrContainer, 'flotr:mousemove',
                this._flotrContainerMouseMoveListener);

            this._previousHighlightedDot = null;
            this._highlightRadius = 7;
            this._highlightLineWidth = 1;
            this._highlightColor = "#ff0000";
            this._highlightFill = "#ffffff";

            this._renderFlotr();
          },

          deinitialize : function($super) {
            $super();

            Event.stopObserving(this._flotrContainer, 'flotr:click',
                this._flotrContainerClickListener);
            Event.stopObserving(this._flotrContainer, 'flotr:mousemove',
                this._flotrContainerMouseMoveListener);
          },

          getMinX : function() {
            return this._minX;
          },

          getMaxX : function() {
            return this._maxX;
          },

          getMinY : function() {
            return this._minY;
          },

          getMaxY : function() {
            return this._maxY;
          },

          getSelectedX : function() {
            return this._parseSelectedX();
          },

          getSelectedY : function() {
            return this._parseSelectedY();
          },

          _getDefaultX : function() {
            return Math.floor((this.getMaxX() - this.getMinX()) / 2);
          },

          _getDefaultY : function() {
            return Math.floor((this.getMaxY() - this.getMinY()) / 2);
          },

          _parseSelectedX : function() {
            var result = parseInt(this._xValueInput.value);
            if (isNaN(result)) {
              return null;
            }

            return result;
          },

          _parseSelectedY : function() {
            var result = parseInt(this._yValueInput.value);
            if (isNaN(result)) {
              return null;
            }

            return result;
          },

          _renderFlotr : function() {
            this._flotr = Flotr.draw(this._flotrContainer, [ {
              data : this._dataSerie,
              lines : {
                show : true,
                fill : false
              },
              points : {
                "show" : true,
                "radius" : 7,
                "lineWidth" : 3,
                "fill" : true,
                "fillColor" : "#FFFFFF",
                "fillOpacity" : 0.4
              }
            } ], this._flotrOptions);
          },

          _getNearestCoordinates : function(position) {
            return {
              x : this._getNearestValue(position.x, this._minX, this._maxX),
              y : this._getNearestValue(position.y, this._minY, this._maxY)
            };
          },

          _getNearestValue : function(value, min, max) {
            if (value <= (min + 0.5)) {
              return min;
            } else if (value >= (max - 0.5)) {
              return max;
            } else {
              for (var v = min; v < max; v++) {
                var v1 = v - 0.5;
                var v2 = v1 + 1;
                if ((value >= v1) && (value <= v2)) {
                  return v;
                }
              }
            }

            return null;
          },

          _onFlotrContainerClick : function(event) {
            var position = event.memo[0];
            var nearestCoords = this._getNearestCoordinates(position);

            this._xValueInput.value = nearestCoords.x;
            this._yValueInput.value = nearestCoords.y;

            this._dataSerie = [ [ nearestCoords.x, nearestCoords.y ] ];
            this._renderFlotr();

            this.fire("valueChange", {
              valueX : nearestCoords.x,
              valueY : nearestCoords.y
            });
          },

          _onFlotrContainerMouseMove : function(event) {
            var position = event.memo[1];
            var nearestCoords = this._getNearestCoordinates(position);

            var series = this._flotr.series;
            var xa = series[0].xaxis;
            var ya = series[0].yaxis;

            this._flotr.octx.save();
            this._flotr.octx.translate(this._flotr.plotOffset.left,
                this._flotr.plotOffset.top);

            if (this._previousHighlightedDot) {
              this._flotr.octx.clearRect(this._previousHighlightedDot.x
                  - (this._highlightRadius + this._highlightLineWidth),
                  this._previousHighlightedDot.y
                      - (this._highlightRadius + this._highlightLineWidth),
                  (this._highlightRadius + this._highlightLineWidth) * 2,
                  (this._highlightRadius + this._highlightLineWidth) * 2);

              this._previousHighlightedDot = null;
            }

            var hitX = xa.d2p(nearestCoords.x);
            var hitY = ya.d2p(nearestCoords.y);

            this._flotr.octx.beginPath();
            this._flotr.octx.strokeStyle = this._highlightColor;
            this._flotr.octx.lineWidth = this._highlightLineWidth;

            this._flotr.octx.fillStyle = this._highlightFill;
            this._flotr.octx.arc(hitX, hitY, this._highlightRadius, 0,
                2 * Math.PI);
            if (this._highlightFill)
              this._flotr.octx.fill();
            this._flotr.octx.stroke();
            this._flotr.octx.closePath();
            this._flotr.octx.restore();

            this._previousHighlightedDot = {
              x : hitX,
              y : hitY
            };
          }
        });

QueryBlockMultiSelectFragmentController = Class.create(QueryBlockFragmentController, {
  initialize : function($super, element) {
    $super('multiselect', element);
    this._itemChangeListener = this._onItemChangeListener.bindAsEventListener(this);
    this.getElement().select('input.queryMultiselectListItemInput').invoke("observe", "change", this._itemChangeListener);
  },
  
  deinitialize : function($super) {
    this.getElement().select('input.queryMultiselectListItemInput').invoke("purge");
    $super();
  },
  
  getLabels : function() {
    var result = [];
    var labels = this.getElement().select('label.queryMultiselectListItemLabel');
    for (var i = 0, l = labels.length; i < l; i++) {
      result.push(labels[i].innerHTML);
    }

    return result;
  },
  
  getSelectedIndexes : function() {
    var result = [];
    var inputs = this.getElement().select('input.queryMultiselectListItemInput');
    for (var i = 0, l = inputs.length; i < l; i++) {
      if (inputs[i].checked) {
        result.push(i);
      }
    }

    return result;
  },
  
  _onItemChangeListener : function() {
    this.fire("valueChange", {});
  }
});

QueryCommentsController = Class.create({
  setup : function() {
    this._saveCommentReplyClickListener = this._onPostReplyClick.bindAsEventListener(this);
    this._commentReplyClickListener = this._onReplyClick.bindAsEventListener(this);
    this._commentEditLinkClickListener = this._onCommentEditLinkClick.bindAsEventListener(this);
    this._commentHideLinkClickListener = this._onCommentHideLinkClick.bindAsEventListener(this);
    this._commentShowLinkClickListener = this._onCommentShowLinkClick.bindAsEventListener(this);
    this._commentDeleteLinkClickListener = this._onCommentDeleteLinkClick.bindAsEventListener(this);
    this._newReplyCountInput = $('newRepliesCount');
    this._toggleCommentShowHideButtonClickListener = this._onToggleCommentShowHideButtonClickListener.bindAsEventListener(this);
    var _this = this;
    
    $$('.queryCommentNewCommentLink').each(function(node) {
      Event.observe(node, 'click', _this._commentReplyClickListener);
    });

    $$('.queryCommentHideCommentLink').each(function(node) {
      Event.observe(node, 'click', _this._commentHideLinkClickListener);
    });

    $$('.queryCommentShowCommentLink').each(function(node) {
      Event.observe(node, 'click', _this._commentShowLinkClickListener);
    });

    $$('.queryCommentEditCommentLink').each(function(node) {
      Event.observe(node, 'click', _this._commentEditLinkClickListener);
    });

    $$('.queryCommentDeleteCommentLink').each(function(node) {
      Event.observe(node, 'click', _this._commentDeleteLinkClickListener);
    });

    $$("div.queryCommentShowHideButton").each(function(node) {
      Event.observe(node, "click", _this._toggleCommentShowHideButtonClickListener);
    });

  },
  deinitialize : function() {
    var _this = this;
    $$('.queryCommentNewCommentLink').each(function(node) {
      Event.stopObserving(node, 'click', _this._commentReplyClickListener);
    });

    $$('.queryCommentHideCommentLink').each(function(node) {
      Event.stopObserving(node, 'click', _this._commentHideLinkClickListener);
    });

    $$('.queryCommentShowCommentLink').each(function(node) {
      Event.stopObserving(node, 'click', _this._commentShowLinkClickListener);
    });

    $$('.queryCommentEditCommentLink').each(function(node) {
      Event.stopObserving(node, 'click', _this._commentEditLinkClickListener);
    });

    $$('.queryCommentDeleteCommentLink').each(function(node) {
      Event.stopObserving(node, 'click', _this._commentDeleteLinkClickListener);
    });

    $$("div.queryCommentShowHideButton").each(function(node) {
      Event.observe(node, "click", _this._toggleCommentShowHideButtonClickListener);
    });
  },
  
  _onToggleCommentShowHideButtonClickListener : function(event) {
    var element = Event.element(event);
    Event.stop(event);
    var commentContainer = element.up(".queryComment").down(".queryCommentContainerWrapper");
    
    if (commentContainer.visible()) {
      commentContainer.fade();
      element.removeClassName("hideIcon");
      element.addClassName("showIcon");
    } else {
      commentContainer.appear();
      element.removeClassName("showIcon");
      element.addClassName("hideIcon");
    }
  },
  
  _onReplyClick : function(event) {
    Event.stop(event);
    var commentElement = Event.element(event).up(".queryComment");

    var oldEditor = commentElement.down(".newCommentEditor");
    if ((oldEditor != undefined) && (oldEditor.parentNode == commentElement)) {
      return;
    }
    
    var replyNum = parseInt(this._newReplyCountInput.value);
    this._newReplyCountInput.value = replyNum + 1;

    var parentCommentId = commentElement.down("input[name='commentId']").value;
    var editorElement = new Element("div", {
      className : "newCommentEditor"
    });
    
    var textEditorElement = new Element("textarea", {
      name : "commentReply." + replyNum
    });
    
    var newReplyParentCommentElement = new Element("input", {
      name : "commentReplyParent." + replyNum,
      value : parentCommentId,
      type : "hidden"
    });
    
    var saveButtonElement = new Element("input", {
      type : "button",
      className : "formButton",
      value : getLocale().getText("query.comment.saveCommentButton")
    });

    editorElement.appendChild(textEditorElement);
    editorElement.appendChild(saveButtonElement);
    editorElement.appendChild(newReplyParentCommentElement);

    Event.observe(saveButtonElement, 'click', this._saveCommentReplyClickListener);

    commentElement.appendChild(editorElement);
  },
  
  _onPostReplyClick : function(event) {
    var saveButton = Event.element(event);
    saveButton.writeAttribute("disabled", "disabled");
    
    var commentElement = saveButton.up(".queryComment");
    var newCommentElement = saveButton.up(".newCommentEditor");
    var textArea = newCommentElement.down("textarea");
    var queryPageId = commentElement.down("input[name='queryPageId']").value;
    var parentCommentId = commentElement.down("input[name='commentId']").value;
    var comment = textArea.value;
    var _this = this;

    JSONUtils.request(CONTEXTPATH + '/queries/savecomment.json', {
      parameters : {
        queryPageId : queryPageId,
        parentCommentId : parentCommentId,
        comment : comment
      },
      onSuccess : function(jsonRequest) {
        var newComment = new Element("div", {
          className : "queryComment"
        });
        
        var newCommentHeader = new Element("div", {
          className : "queryCommentHeader"
        });
        
        var newCommentWrapper = new Element("div", {
          className : "queryCommentContainerWrapper"
        });
        
        var newCommentMeta = new Element("div", {
          className : "queryCommentMeta"
        });
        
        var newCommentText = new Element("div", {
          className : "queryCommentText"
        });
        
        var newCommentDate = new Element("div", {
          className : "queryCommentDate"
        });
        
        var commentDate = new Date();
        newCommentText.update(comment.replace(/\n/g, '<br/>'));
        newCommentDate.update(getLocale().getText("query.comment.commentDate") + " " + getLocale().getDate(commentDate));
          
        var childrenContainer = new Element("div", {
          className : "queryCommentChildren",
          id : "queryCommentChildren." + jsonRequest.commentId
        });
        
        var newCommentAnchor = new Element("a", {
          id : "comment." + jsonRequest.commentId
        });
        
        var newCommentHideShowButton = new Element("div", {
          className : "queryCommentShowHideButton hideIcon"
        });
        
        newCommentHeader.appendChild(newCommentDate);
        _this._createCommentLinks(newCommentMeta);
        newComment.appendChild(newCommentAnchor);
        newComment.appendChild(newCommentHideShowButton);
        newComment.appendChild(newCommentHeader);
        newComment.appendChild(newCommentWrapper);

        newCommentWrapper.appendChild(new Element("input", {
          type : "hidden",
          name : "commentId",
          value : jsonRequest.commentId
        }));
        
        newCommentWrapper.appendChild(new Element("input", {
          type : "hidden",
          name : "queryPageId",
          value : jsonRequest.queryPageId
        }));

        newCommentWrapper.appendChild(newCommentText);
        newCommentWrapper.appendChild(newCommentMeta);

        newCommentWrapper.appendChild(childrenContainer);

        var childrenParent = $('queryCommentChildren.' + parentCommentId);
        if (childrenParent) {
          childrenParent.appendChild(newComment);
        }
            
        Event.observe(newCommentHideShowButton, "click", _this._toggleCommentShowHideButtonClickListener);
        Event.stopObserving(newCommentElement.down("input"), 'click', _this._saveCommentReplyClickListener);
        newCommentElement.remove();
      }
    });
  },
  
  _createCommentLink : function(parent, containerClass, linkClass, linkText, listener) {
    var container = new Element("div", {
      className : containerClass
    });
    
    var link = new Element("a", {
      href : "#",
      className : linkClass
    });
    
    container.appendChild(link.update(linkText));

    parent.appendChild(container);
    Event.observe(link, 'click', listener);

    return container;
  },
  
  _createCommentLinks : function(container) {
    this._createCommentLink(container, "queryCommentNewComment", "queryCommentNewCommentLink", getLocale().getText("query.comment.commentAnswerLink"), this._commentReplyClickListener);
    if (JSDATA['canManageComments'] == 'true') {
      this._createCommentLink(container, "queryCommentShowComment", "queryCommentShowCommentLink", getLocale().getText("query.comment.commentShowLink"), this._commentShowLinkClickListener);
      this._createCommentLink(container, "queryCommentHideComment", "queryCommentHideCommentLink", getLocale().getText("query.comment.commentHideLink"), this._commentHideLinkClickListener);
      this._createCommentLink(container, "queryCommentEditComment", "queryCommentEditCommentLink", getLocale().getText("query.comment.commentEditLink"), this._commentEditLinkClickListener);
      this._createCommentLink(container, "queryCommentDeleteComment", "queryCommentDeleteCommentLink", getLocale().getText("query.comment.commentDeleteLink"), this._commentDeleteLinkClickListener);
    }
  },
  
  _onCommentEditLinkClick : function(event) {
    Event.stop(event);
    var commentElement = Event.element(event).up(".queryComment");

    var oldEditor = commentElement.down(".editCommentEditor");
    if ((oldEditor != undefined) && (oldEditor.parentNode == commentElement.down(".queryCommentContainerWrapper"))) {
      return;
    }
    
    var commentId = commentElement.down("input[name='commentId']").value;
    var commentTextElement = commentElement.down(".queryCommentText");
    var commentText = commentTextElement.innerHTML.replace(/<br\/>/ig, '\n').replace(/<br>/ig, '\n');
    var editorElement = new Element("div", {
      className : "editCommentEditor"
    });
    
    var textEditorElement = new Element("textarea", {
      name : "commentEditor." + commentId
    }).update(commentText);
    
    var saveButtonElement = new Element("input", {
      type : "button",
      className : "formButton",
      value : getLocale().getText("query.comment.saveCommentButton")
    });

    commentTextElement.hide();
    editorElement.appendChild(textEditorElement);
    editorElement.appendChild(saveButtonElement);
    commentTextElement.insert({
      before : editorElement
    });

    Event.observe(saveButtonElement, "click", function(event) {
      Event.stop(event);
      saveButtonElement.writeAttribute("disabled", "disabled");
      
      var element = Event.element(event);
      var newText = textEditorElement.value;
      startLoadingOperation('query.comment.updatingComment');
      JSONUtils.request(CONTEXTPATH + '/queries/updatecomment.json', {
        parameters : {
          commentId : commentId,
          comment : newText
        },
        onComplete : function() {
          endLoadingOperation();
        },
        onSuccess : function() {
          if (!commentElement.down(".queryCommentModified")) {
            var header = commentElement.down(".queryCommentHeader");
            header.insert({
              after : new Element("div", {
              className : "queryCommentModified"
            }).update(getLocale().getText('query.comment.commentModified', [ getLocale().getDate(new Date().getTime()) ])) });
          }

          element.purge();
          editorElement.remove();
          commentTextElement.show();
          commentTextElement.update(newText.replace(/\n/g, '<br/>'));
        }
      });
    });
  },
  
  _onCommentHideLinkClick : function(event) {
    Event.stop(event);
    var commentElement = Event.element(event).up(".queryComment");
    var commentId = commentElement.down("input[name='commentId']").value;
    JSONUtils.request(CONTEXTPATH + '/queries/hidecomment.json', {
      parameters : {
        commentId : commentId
      },
      onSuccess : function() {
        commentElement.addClassName('queryCommentHidden');
      }
    });
  },
  
  _onCommentShowLinkClick : function(event) {
    Event.stop(event);
    var commentElement = Event.element(event).up(".queryComment");
    var commentId = commentElement.down("input[name='commentId']").value;
    
    JSONUtils.request(CONTEXTPATH + '/queries/showcomment.json', {
      parameters : {
        commentId : commentId
      },
      onSuccess : function() {
        commentElement.removeClassName('queryCommentHidden');
      }
    });
  
  },
  
  _onCommentDeleteLinkClick : function(event) {
    Event.stop(event);
    
    var linkElement = Event.element(event);
    var commentElement = linkElement.up(".queryComment");

    var commentId = commentElement.down("input[name='commentId']").value;
    var commentText = commentElement.down(".queryCommentText").innerHTML;
    if (!commentText) {
      commentText = '-';
    }

    var popup = new ModalPopup({
      content : getLocale().getText('query.comment.deleteCommentDialogText', [ commentText.truncate(15) ]),
      buttons : [{
        text : getLocale().getText('query.comment.deleteCommentDialogCancelButton'),
        action : function(instance) {
          instance.close();
        }
      }, {
        text : getLocale().getText('query.comment.deleteCommentDialogDeleteButton'),
        classNames : "modalPopupButtonRed",
        action : function(instance) {
          startLoadingOperation('query.comment.deletingComment');
          instance.close(true);
          
          JSONUtils.request(CONTEXTPATH + '/queries/deletecomment.json', {
            parameters : {
              commentId : commentId
            },
            onComplete : function() {
              endLoadingOperation();
            },
            onSuccess : function() {
              commentElement.remove();
            }
          });
        }
      }]
    });

    popup.open(linkElement);
  }
});

QueryBlockOrderingFragmentController = Class
    .create(
        QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('orderingField', element);

            this._itemUpClickListener = this._onItemUpClick
                .bindAsEventListener(this);
            this._itemDownClickListener = this._onItemDownClick
                .bindAsEventListener(this);

            this._orderInput = this.getElement().down("input[name='order']");

            var _this = this;

            this.getElement().select(".queryOrderingFieldItemContainer").each(
                function(node) {
                  var upBtn = node.down(".queryOrderingFieldItemMoveUpButton");
                  var downBtn = node
                      .down(".queryOrderingFieldItemMoveDownButton");

                  if (upBtn != undefined)
                    Event.observe(upBtn, 'click', _this._itemUpClickListener);
                  if (downBtn != undefined)
                    Event.observe(downBtn, 'click',
                        _this._itemDownClickListener);
                });
          },
          deinitialize : function($super) {
            $super();

            var _this = this;
            this.getElement().select(".queryOrderingFieldItemContainer").each(
                function(node) {
                  var upBtn = node.down(".queryOrderingFieldItemMoveUpButton");
                  var downBtn = node
                      .down(".queryOrderingFieldItemMoveDownButton");

                  if (upBtn != undefined)
                    Event.stopObserving(upBtn, 'click',
                        _this._itemUpClickListener);
                  if (downBtn != undefined)
                    Event.stopObserving(downBtn, 'click',
                        _this._itemDownClickListener);
                });
          },
          getOrder : function() {
            var result = new Array();
            var order = this._orderInput.value.split(',');
            for (var i = 0, l = order.length; i < l; i++) {
              result.push(parseInt(order[i]));
            }

            return result;
          },
          _refreshInternalValue : function() {
            var value = "";

            this.getElement().select(".queryOrderingFieldItemContainer").each(
                function(node) {
                  var v = node.down("input[name='itemName']").value;

                  if (value != "")
                    value = value + "," + v;
                  else
                    value = v;
                });

            this._orderInput.value = value;

            this.fire("valueChange", {});
          },
          _swap : function(itemElement, substitution) {
            var text1 = itemElement.down(".queryOrderingFieldItemText").innerHTML;
            var text2 = substitution.down(".queryOrderingFieldItemText").innerHTML;
            var id1 = itemElement.down("input[name='itemName']").value;
            var id2 = substitution.down("input[name='itemName']").value;

            itemElement.down(".queryOrderingFieldItemText").update(text2);
            substitution.down(".queryOrderingFieldItemText").update(text1);
            itemElement.down("input[name='itemName']").value = id2;
            substitution.down("input[name='itemName']").value = id1;
          },
          _onItemUpClick : function(event) {
            var btn = Event.element(event);

            var itemElement = btn.up(".queryOrderingFieldItemContainer");
            var substitution = itemElement
                .previous(".queryOrderingFieldItemContainer");

            if (substitution != undefined)
              this._swap(itemElement, substitution);

            this._refreshInternalValue();
          },
          _onItemDownClick : function(event) {
            var btn = Event.element(event);

            var itemElement = btn.up(".queryOrderingFieldItemContainer");
            var substitution = itemElement
                .next(".queryOrderingFieldItemContainer");

            if (substitution != undefined)
              this._swap(itemElement, substitution);

            this._refreshInternalValue();
          }
        });

QueryBlockGroupingFragmentController = Class
    .create(
        QueryBlockFragmentController,
        {
          initialize : function($super, element) {
            $super('grouping', element);

            var _this = this;
            this._removeGroupItemClickListener = this._onRemoveGroupItemClick
                .bindAsEventListener(this);
            this._removeGroupItemTouchEndListener = this._onRemoveGroupItemTouchEnd
                .bindAsEventListener(this);

            this.getElement().down(".queryGroupingItemContainer").select(
                ".queryGroupingItem").each(function(node) {
              node._controller = _this;

              new Draggable(node, {
                revert : true,
                ghosting : true,
                endeffect : Prototype.emptyFunction
              });
            });

            this.getElement().down(".queryGroupingGroupContainer").select(
                ".queryGroupingGroupItemContainer").each(
                function(node) {
                  Droppables.add(node, {
                    onDrop : _this._onDroppedName
                  });

                  node.select(".queryGroupingItem").each(
                      function(node) {
                        node._controller = _this;

                        var remove = node
                            .down(".queryMultiselectListItemRemove");
                        Event.observe(remove, "click",
                            _this._removeGroupItemClickListener);
                        Event.observe(remove, "touchend",
                            _this._removeGroupItemTouchEndListener);

                        node._dragIntf = new Draggable(node, {
                          revert : true,
                          ghosting : true,
                          endeffect : Prototype.emptyFunction
                        });
                      });
                });
          },
          deinitialize : function($super) {
            $super();
          },
          getValueSet : function() {
            return $H($('groupingFieldValue').value.evalJSON());
          },
          _removeGroupItem : function(element) {
            var groupingItem = element.up(".queryGroupingItem");
            groupingItem._dragIntf.destroy();
            groupingItem.purge();
            groupingItem.remove();

            this._refreshInternalValue();
          },
          _onDroppedName : function(draggable, droparea) {
            var controller = draggable._controller;

            var text = draggable.down("label").innerHTML;
            var name = draggable.down("input[name='queryGroupingItemId']").value;

            // Check that the item doesn't exist already
            var found = droparea
                .down("input[name='queryGroupingItemId'][value='" + name + "']");

            if (found == undefined) {
              var dropNode = draggable;

              // If dragged item is dragged from item list, it's cloned,
              // otherwise moved
              if (draggable.up(".queryGroupingGroupItemContainer") == undefined)
                dropNode = controller._createItemDiv(text, name);

              droparea.appendChild(dropNode);

              controller._refreshInternalValue();
            }
          },
          _onRemoveGroupItemClick : function(event) {
            var element = Event.element(event);
            this._removeGroupItem(element);
          },
          _onRemoveGroupItemTouchEnd : function(event) {
            var element = Event.element(event);
            this._removeGroupItem(element);
          },
          _createItemDiv : function(text, name) {
            var root = new Element("div", {
              className : "queryGroupingItem"
            });
            var label = new Element("label", {
              className : "queryMultiselectListItemLabel"
            });
            var remove = new Element("div", {
              className : "queryMultiselectListItemRemove"
            });
            var input = new Element("input", {
              type : "hidden",
              name : "queryGroupingItemId",
              value : name
            });

            label.update(text);

            root._controller = this;
            root.appendChild(label);
            root.appendChild(input);
            root.appendChild(remove);
            Event.observe(remove, "click", this._removeGroupItemClickListener);
            Event.observe(remove, "touchend",
                this._removeGroupItemTouchEndListener);

            root._dragIntf = new Draggable(root, {
              revert : true,
              ghosting : true
            });

            return root;
          },
          _refreshInternalValue : function() {
            var valueSet = new Hash();

            this
                .getElement()
                .down(".queryGroupingGroupContainer")
                .select(".queryGroupingGroup")
                .each(
                    function(node) {
                      var itemsArr = new Array();
                      var groupId = node
                          .down("input[name='queryGroupingGroupId']").value;

                      node
                          .select(".queryGroupingItem")
                          .each(
                              function(node) {
                                var itemId = node
                                    .down("input[name='queryGroupingItemId']").value;
                                itemsArr.push(itemId);
                              });

                      valueSet.set(groupId, itemsArr);
                    });

            $('groupingFieldValue').value = Object.toJSON(valueSet);

            this.fire("valueChange", {
              valueSet : valueSet
            });
          }
        });

/* Live Report Controllers */

QueryLiveReportController = Class.create(QueryBlockFragmentController, {
  initialize : function($super, name, element) {
    $super(name, element);
  }
});

QueryBarChartLiveReportController = Class.create(QueryLiveReportController, {
  initialize : function($super, name, element, minX, maxX, tickLabels) {
    $super(name, element);

    this._flotrContainer = element
        .down('.queryBarChartLiveReportFlotrContainer');
    this._axisLabel = this.getElement().down('input[name="axisLabel"]').value;

    this._minX = minX - 1;
    this._maxX = maxX + 1;
    this._minY = 0;
    this._maxY = 100;
    this._xTickLabels = new Array();
    this._dataSeries = new Array();
    this._userDataSeries = new Array();

    for (var i = minX, l = maxX + 1; i < l; i++) {
      var reportValue = element.down('input.reportValue[name="reportValue.' + i
          + '"]');
      var tickLabel = tickLabels[i];
      if (tickLabel)
        this._xTickLabels.push([ i, tickLabel ]);
      this._dataSeries.push(reportValue ? parseInt(reportValue.value) : 0);
      this._userDataSeries.push(0);
    }
  },
  deinitialize : function($super) {
    $super();
  },
  setUserData : function(index, value) {
    this._userDataSeries[index] = parseFloat(value);
  },
  _getDataSeries : function() {
    var total = 0;

    for (var i = 0, l = this._dataSeries.length; i < l; i++) {
      total += this._dataSeries[i] + this._userDataSeries[i];
    }

    var result = new Array();

    if (total > 0) {
      var max = 0;

      for (var i = 0, l = this._dataSeries.length; i < l; i++) {
        var data = new Array();

        var massValue = this._dataSeries[i];
        var massPercent = (massValue / total) * 100;
        var userValue = this._userDataSeries[i];
        var userPercent = (userValue / total) * 100;

        data.push([ i, massPercent ]);

        data.push([ i, userPercent ]);

        max = Math.max(max, massPercent + userPercent);

        result.push({
          "data" : data
        });
      }

      this._maxY = (max * 1.2);
    } else {
      this._maxY = 100;
      result.push({
        "data" : []
      });
    }

    return result;
  },
  draw : function() {
    this._flotr = Flotr.draw(this._flotrContainer, this._getDataSeries(), {
      "xaxis" : {
        "min" : this._minX,
        "max" : this._maxX,
        "ticks" : this._xTickLabels,
        "title" : this._axisLabel
      },
      "yaxis" : {
        "min" : this._minY,
        "max" : this._maxY,
        "tickDecimals" : 2,
        "tickFormatter" : function(val) {
          return val + " %";
        }
      },
      "lines" : {
        "show" : false
      },
      "bars" : {
        "show" : true,
        "barWidth" : 0.8,
        "stacked" : true
      },
      "markers" : {
        "show" : true,
        "labelFormatter" : function(val) {
          return val.y > 0 ? (Math.round(val.y * 100) / 100) + " %" : "";
        }
      }
    });
  }
});

QueryBubbleChartLiveReportController = Class.create(QueryLiveReportController, {
  initialize : function($super, name, element, minX, maxX, minY, maxY) {
    $super(name, element);

    this._flotrContainer = element.down('.queryBubbleChartLiveReportFlotrContainer');

    this._minX = minX;
    this._minY = minY;
    this._maxX = maxX;
    this._maxY = maxY;
    this._userData = [ [ 0, 0, 0 ] ];

    this._data = [];
    this._xTickLabels = [];
    this._yTickLabels = [];
    this._xAxisLabel = this.getElement().down('input[name="xAxisLabel"]').value;
    this._yAxisLabel = this.getElement().down('input[name="yAxisLabel"]').value;
    var i, l;

    var xLabels = this.getElement().select("input.bubbleLabelX");
    for (i = 0, l = xLabels.length; i < l; i++) {
      this._xTickLabels.push([ i, xLabels[i].value ]);
    }

    var yLabels = this.getElement().select("input.bubbleLabelY");
    for (i = 0, l = yLabels.length; i < l; i++) {
      this._yTickLabels.push([ i, yLabels[i].value ]);
    }

    var bubbleValues = this.getElement().select("input.bubbleValue");
    for (i = 0, l = bubbleValues.length; i < l; i++) {
      var bubbleValue = bubbleValues[i];
      var axisNames = bubbleValue.name.split('.');
      var xIndex = parseInt(axisNames[1]);
      var yIndex = parseInt(axisNames[2]);
      var bubbleX = this.getElement().down('input[name="bubble.' + xIndex + '.' + yIndex + '.x"]');
      var bubbleY = this.getElement().down('input[name="bubble.' + xIndex + '.' + yIndex + '.y"]');
      var x = parseInt(bubbleX.value);
      var y = parseInt(bubbleY.value);
      var z = parseFloat(bubbleValue.value);
      this._data.push([ x, y, z ]);
    }
  },
  deinitialize : function($super) {
    $super();
  },
  setUserValues : function(values) {
    this._userData = values;
  },
  draw : function() {
    var options = {
      "xaxis" : {
        "min" : this._minX,
        "max" : this._maxX,
        "title" : this._xAxisLabel
      },
      "yaxis" : {
        "min" : this._minY,
        "max" : this._maxY,
        "title" : this._yAxisLabel
      },
      "bubbles" : {
        "show" : true,
        "baseRadius" : 5
      },
      "markers" : {
        "show" : true,
        "labelFormatter" : function(val) {
          var index = val.index;
          var value = val.data[index][2];
          return value > 0 ? value : "";
        }
      }
    };

    if (this._xTickLabels && this._xTickLabels.length > 0) {
      options["xaxis"]["ticks"] = this._xTickLabels;
    }
    
    if (this._yTickLabels && this._yTickLabels.length > 0) {
      options["yaxis"]["ticks"] = this._yTickLabels;
    }
    
    this._flotr = Flotr.draw(this._flotrContainer, this._getDataSeries(), options);
  },
  
  _getQuartileValues: function (values, data, axis) {
    for (var i = 0; i < data.length; i++) {
      for (var x = 0; x < data[i][2]; x++) {
        values.push(data[i][axis]);
      }
    } 
  },
  
  _getQuartile: function (axis, lowerPercent) {
    var values = [];
    
    this._getQuartileValues(values, this._data, axis);
    this._getQuartileValues(values, this._userData, axis);
    values.sort();

    var index = Math.round(values.length * lowerPercent / 100);
    return values[index];
  },
  
  _addMarkerLineX: function (data, label, value) {
    data.push({ 
      data: [ [ Number.MIN_VALUE, value, 0 ], [ Number.MAX_VALUE, value, 0 ] ],
      label: label,
      lines: {
        show: true
      },
      bubbles: {
        show: false
      },
      markers: {
        show: false
      }
    });
  },
  
  _addMarkerLineY: function (data, label, value) {
    data.push({ 
      data: [ [ value, Number.MIN_VALUE, 0 ], [ value, Number.MAX_VALUE, 0 ] ],
      label: label,
      lines: {
        show: true
      },
      bubbles: {
        show: false
      },
      markers: {
        show: false
      }
    });
  },
  
  _getDataSeries : function() {
    var data = [];
    var userDataFound = false;

    for (var i = 0, l = this._data.length; i < l; i++) {
      var dataObject = {};
      dataObject[0] = this._data[i][0];
      dataObject[1] = this._data[i][1];

      var userValue = 0;
      for (var j = 0, jl = this._userData.length; j < jl; j++) {
        var userDataObject = this._userData[j];
        if ((dataObject[0] == userDataObject[0]) && (dataObject[1] == userDataObject[1])) {
          userValue++;
          userDataFound = true;
        }
      }

      dataObject[2] = this._data[i][2] + userValue;
      data.push(dataObject);
    }
    
    if (this._userData && this._userData[0] && this._userData[0][0] && this._userData[0][1]) {
      if (!userDataFound && (this._userData.length > 0)) {
        data.push({
          0: this._userData[0][0],
          1: this._userData[0][1],
          2: this._userData[0][2]
        });
      }

      let result = [{ data: data }, { data: this._userData } ];
      
      return result;   
    } else {
      return [{ data: data }, { data: [[ 0, 0, 0 ]] } ];
    }

  }
});

QueryPieChartLiveReportController = Class.create(QueryLiveReportController, {
  initialize : function($super, name, element, caption, ids, values, captions) {
    $super(name, element);

    this._flotrContainer = element
        .down('.queryPieChartLiveReportFlotrContainer');
    this._caption = caption;
    this._ids = ids;
    this._values = values;
    this._captions = captions;
    this._userData = [];
  },
  deinitialize : function($super) {
    $super();
  },
  setUserData : function(data) {
    this._userData = data;
  },
  _getDataSeries : function() {
    var result = new Array();
    for (var i = 0, l = this._values.length; i < l; i++) {
      var id = this._ids[i];
      var value = this._values[i];

      if (this._userData.indexOf(id) > -1) {
        value++;
      }

      result.push({
        label : this._captions[i],
        data : [ [ 0, value ] ]
      });
    }
    ;

    return result;
  },
  draw : function() {
    this._flotr = Flotr.draw(this._flotrContainer, this._getDataSeries(), {
      "title" : this._caption,
      "pie" : {
        "show" : true
      },
      "xaxis" : {
        "showLabels" : false
      },
      "yaxis" : {
        "showLabels" : false
      },
      "grid" : {
        "verticalLines" : false,
        "horizontalLines" : false
      }
    });
  }
});

QueryStackedBarChartLiveReportController = Class.create(
    QueryLiveReportController, {
      initialize : function($super, name, element) {
        $super(name, element);

        this._flotrContainer = element
            .down('.queryStackedBarChartLiveReportFlotrContainer');

        var labelInputs = element.select('.reportItemLabel');
        var valuesInputs = element.select('.reportItemValues');

        if (labelInputs.length != valuesInputs.length) {
          throw new Error("label count does not match values count");
        }

        var titleElement = element.down('input[name="title"]');
        this._title = '';

        if (titleElement) {
          this._title = titleElement.value;
          titleElement.remove();
        }

        this._userData = null;
        this._series = new Array();

        for (var i = 0, l = labelInputs.length; i < l; i++) {
          var label = labelInputs[i].value;
          var values = valuesInputs[i].value;

          this._series.push({
            label : label,
            values : values.split(',')
          });
        }

        labelInputs.invoke('remove');
        valuesInputs.invoke('remove');
      },
      deinitialize : function($super) {
        $super();
      },
      setUserData : function(userData) {
        this._userData = userData;
      },
      _getDataSeries : function() {
        var result = new Array();

        for (var i = 0, l = this._series.length; i < l; i++) {
          var data = new Array();

          for (var j = 0; j < l; j++) {
            data.push([ j, 0 ]);
          }

          result.push({
            label : (i + 1),
            data : data
          });
        }

        for (var i = 0, l = this._series.length; i < l; i++) {
          var values = this._series[i].values;
          var userValues = this._userData ? this._userData[i] : null;

          for (var j = 0; j < l; j++) {
            var value = values[j];
            var userValue = userValues ? userValues[j] : 0;

            result[j].data[i][1] = parseFloat(value)
                + (userValue ? userValue : 0);
          }
        }

        return result;
      },
      draw : function() {
        var _this = this;
        this._flotr = Flotr.draw(this._flotrContainer, this._getDataSeries(), {
          "title" : this._title,
          "bars" : {
            "show" : true,
            "barWidth" : 0.8,
            "stacked" : true
          },
          "yaxis" : {
            "showLabels" : false
          },
          "xaxis" : {
            "tickFormatter" : function(val) {
              var rounded = Math.round(val);
              var index = rounded == val ? rounded : -1;
              if (_this._series[index]) {
                return _this._series[index].label;
              }

              return '';
            }
          }
        });
      }
    });

QueryLineChartLiveReportController = Class
    .create(
        QueryLiveReportController,
        {
          initialize : function($super, name, element, minY, maxY) {
            $super(name, element);

            this._title = this._retrieveFieldValue('title');
            this._userDataSetLabel = this
                ._retrieveFieldValue('userDataSetLabel');
            var seriesCount = parseInt(this._retrieveFieldValue('seriesCount'));

            this._dataSeries = new Array();
            this._overriddenValues = new Object();
            this._userData = new Array();

            for (var serieIndex = 0; serieIndex < seriesCount; serieIndex++) {
              var data = new Array();
              var caption = this._retrieveFieldValue('serie.' + serieIndex
                  + '.caption');
              var valueCount = parseInt(this._retrieveFieldValue('serie.'
                  + serieIndex + '.count'));

              for (var valueIndex = 0; valueIndex < valueCount; valueIndex++) {
                var value = parseFloat(this._retrieveFieldValue('serieValue.'
                    + serieIndex + '.' + valueIndex));
                if (!isNaN(value))
                  data.push([ valueIndex, value ]);
              }

              this._dataSeries.push({
                label : caption,
                data : data
              });
            }

            this._xTickLabels = new Array();
            var tickLabelCount = parseInt(this
                ._retrieveFieldValue('tickLabelCount'));
            for (var tickLabelIndex = 0; tickLabelIndex < tickLabelCount; tickLabelIndex++) {
              var tickLabel = this._retrieveFieldValue('tickLabel.'
                  + tickLabelIndex);
              this._xTickLabels.push([ tickLabelIndex, tickLabel ]);
            }
            this._minX = 0;
            this._maxX = this._xTickLabels.length - 1;
            this._minY = minY;
            this._maxY = maxY;

            this._flotrContainer = element
                .down('.queryLineChartLiveReportFlotrContainer');
          },
          deinitialize : function($super) {
            $super();
          },
          _retrieveFieldValue : function(name) {
            var field = this.getElement().down('input[name="' + name + '"]');
            if (field) {
              var value = field.value;
              field.remove();
              return value;
            }

            return null;
          },
          setUserData : function(data) {
            this._userData = data;
          },
          getData : function(index) {
            return this._dataSeries[index].data;
          },
          clearOverriddenValues : function() {
            this._overriddenValues = new Object();
          },
          setOverriddenValue : function(serieIndex, index, x, y) {
            if (!this._overriddenValues[serieIndex]) {
              this._overriddenValues[serieIndex] = new Object();
            }

            this._overriddenValues[serieIndex][index] = [ x, y ];
          },
          _getDataSeries : function() {
            var result = new Array(this._dataSeries.length);

            for (var i = 0, l = this._dataSeries.length; i < l; i++) {
              result[i] = {
                label : this._dataSeries[i].label,
                data : new Array(this._dataSeries[i].data.length)
              };

              for (var j = 0, jl = this._dataSeries[i].data.length; j < jl; j++) {
                result[i].data[j] = this._dataSeries[i].data[j].clone();
              }
            }

            for ( var serieIndex in this._overriddenValues) {
              serieIndex = parseInt(serieIndex);
              var values = this._overriddenValues[serieIndex];
              for ( var index in values) {
                var value = values[index];
                result[serieIndex].data[index][value[0], 1] = value[1];
              }
            }

            if (this._userData) {
              result.push({
                label : this._userDataSetLabel,
                lines : {
                  "lineWidth" : 3
                },
                data : this._userData
              });
            }

            return result;
          },
          draw : function() {
            this._flotr = Flotr.draw(this._flotrContainer, this
                ._getDataSeries(), {
              "title" : this._title,
              "points" : {
                "show" : true
              },
              "lines" : {
                "show" : true
              },
              "xaxis" : {
                "showLabels" : true,
                "ticks" : this._xTickLabels
              },
              "yaxis" : {
                "showLabels" : true,
                "min" : this._minY,
                "max" : this._maxY
              },
              "grid" : {
                "verticalLines" : true,
                "horizontalLines" : true
              }
            });
          }
        });
