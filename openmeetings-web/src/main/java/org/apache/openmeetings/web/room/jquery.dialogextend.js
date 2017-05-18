/*
 * Licensed MIT https://github.com/ROMB/jquery-dialogextend/blob/master/LICENSE.md
 */
(function() {
  var $;

  $ = jQuery;

  $.widget("ui.dialogExtend", {
    version: "2.0.4",
    modes: {},
    options: {
      "closable": true,
      "dblclick": false,
      "titlebar": false,
      "icons": {
        "close": "ui-icon-closethick",
        "restore": "ui-icon-newwin"
      },
      "load": null,
      "beforeRestore": null,
      "restore": null
    },
    _create: function() {
      this._state = "normal";
      if (!$(this.element[0]).data("ui-dialog")) {
        $.error("jQuery.dialogExtend Error : Only jQuery UI Dialog element is accepted");
      }
      this._verifyOptions();
      this._initStyles();
      this._initButtons();
      this._initTitleBar();
      this._setState("normal");
      this._on("load", function(e) {
        return console.log("test", e);
      });
      return this._trigger("load");
    },
    _setState: function(state) {
      $(this.element[0]).removeClass("ui-dialog-" + this._state).addClass("ui-dialog-" + state);
      return this._state = state;
    },
    _verifyOptions: function() {
      var name, _ref, _results;

      if (this.options.dblclick && !(this.options.dblclick in this.modes)) {
        $.error("jQuery.dialogExtend Error : Invalid <dblclick> value '" + this.options.dblclick + "'");
        this.options.dblclick = false;
      }
      if (this.options.titlebar && ((_ref = this.options.titlebar) !== "none" && _ref !== "transparent")) {
        $.error("jQuery.dialogExtend Error : Invalid <titlebar> value '" + this.options.titlebar + "'");
        this.options.titlebar = false;
      }
      _results = [];
      for (name in this.modes) {
        if (this["_verifyOptions_" + name]) {
          _results.push(this["_verifyOptions_" + name]());
        } else {
          _results.push(void 0);
        }
      }
      return _results;
    },
    _initStyles: function() {
      var name, style, _results;

      if (!$(".dialog-extend-css").length) {
        style = '';
        style += '<style class="dialog-extend-css" type="text/css">';
        style += '.ui-dialog .ui-dialog-titlebar-buttonpane>a { float: right; }';
        style += '.ui-dialog .ui-dialog-titlebar-restore { width: 19px; height: 18px; }';
        style += '.ui-dialog .ui-dialog-titlebar-restore span { display: block; margin: 1px; }';
        style += '.ui-dialog .ui-dialog-titlebar-restore:hover,';
        style += '.ui-dialog .ui-dialog-titlebar-restore:focus { padding: 0; }';
        style += '.ui-dialog .ui-dialog-titlebar ::selection { background-color: transparent; }';
        style += '</style>';
        $(style).appendTo("body");
      }
      _results = [];
      for (name in this.modes) {
        _results.push(this["_initStyles_" + name]());
      }
      return _results;
    },
    _initButtons: function() {
      var buttonPane, mode, name, titlebar, _ref,
        _this = this;

      titlebar = $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar");
      buttonPane = $('<div class="ui-dialog-titlebar-buttonpane"></div>').appendTo(titlebar);
      buttonPane.css({
        "position": "absolute",
        "top": "50%",
        "right": "0.3em",
        "margin-top": "-10px",
        "height": "18px"
      });
      titlebar.find(".ui-dialog-titlebar-close").css({
        "position": "relative",
        "float": "right",
        "top": "auto",
        "right": "auto",
        "margin": 0
      }).find(".ui-icon").removeClass("ui-icon-closethick").addClass(this.options.icons.close).end().appendTo(buttonPane).end();
      buttonPane.append('<a class="ui-dialog-titlebar-restore ui-corner-all ui-state-default" href="#"><span class="ui-icon ' + this.options.icons.restore + '" title="restore">restore</span></a>').find('.ui-dialog-titlebar-restore').attr("role", "button").mouseover(function() {
        return $(this).addClass("ui-state-hover");
      }).mouseout(function() {
        return $(this).removeClass("ui-state-hover");
      }).focus(function() {
        return $(this).addClass("ui-state-focus");
      }).blur(function() {
        return $(this).removeClass("ui-state-focus");
      }).end().find(".ui-dialog-titlebar-close").toggle(this.options.closable).end().find(".ui-dialog-titlebar-restore").hide().click(function(e) {
        e.preventDefault();
        return _this.restore();
      }).end();
      _ref = this.modes;
      for (name in _ref) {
        mode = _ref[name];
        this._initModuleButton(name, mode);
      }
      return titlebar.dblclick(function(evt) {
        if (_this.options.dblclick) {
          if (_this._state !== "normal") {
            return _this.restore();
          } else {
            return _this[_this.options.dblclick]();
          }
        }
      }).select(function() {
        return false;
      });
    },
    _initModuleButton: function(name, mode) {
      var buttonPane,
        _this = this;

      buttonPane = $(this.element[0]).dialog("widget").find('.ui-dialog-titlebar-buttonpane');
      return buttonPane.append('<a class="ui-dialog-titlebar-' + name + ' ui-corner-all ui-state-default" href="#" title="' + name + '"><span class="ui-icon ' + this.options.icons[name] + '">' + name + '</span></a>').find(".ui-dialog-titlebar-" + name).attr("role", "button").mouseover(function() {
        return $(this).addClass("ui-state-hover");
      }).mouseout(function() {
        return $(this).removeClass("ui-state-hover");
      }).focus(function() {
        return $(this).addClass("ui-state-focus");
      }).blur(function() {
        return $(this).removeClass("ui-state-focus");
      }).end().find(".ui-dialog-titlebar-" + name).toggle(this.options[mode.option]).click(function(e) {
        e.preventDefault();
        return _this[name]();
      }).end();
    },
    _initTitleBar: function() {
      var handle;

      switch (this.options.titlebar) {
        case false:
          return 0;
        case "none":
          if ($(this.element[0]).dialog("option", "draggable")) {
            handle = $("<div />").addClass("ui-dialog-draggable-handle").css("cursor", "move").height(5);
            $(this.element[0]).dialog("widget").prepend(handle).draggable("option", "handle", handle);
          }
          return $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar").find(".ui-dialog-title").html("&nbsp;").end().css({
            "background-color": "transparent",
            "background-image": "none",
            "border": 0,
            "position": "absolute",
            "right": 0,
            "top": 0,
            "z-index": 9999
          }).end();
        case "transparent":
          return $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar").css({
            "background-color": "transparent",
            "background-image": "none",
            "border": 0
          });
        default:
          return $.error("jQuery.dialogExtend Error : Invalid <titlebar> value '" + this.options.titlebar + "'");
      }
    },
    state: function() {
      return this._state;
    },
    restore: function() {
      this._trigger("beforeRestore");
      this._restore();
      this._toggleButtons();
      return this._trigger("restore");
    },
    _restore: function() {
      if (this._state !== "normal") {
        this["_restore_" + this._state]();
        this._setState("normal");
        return $(this.element[0]).dialog("widget").focus();
      }
    },
    _saveSnapshot: function() {
      if (this._state === "normal") {
        this.original_config_resizable = $(this.element[0]).dialog("option", "resizable");
        this.original_config_draggable = $(this.element[0]).dialog("option", "draggable");
        this.original_size_height = $(this.element[0]).dialog("widget").outerHeight();
        this.original_size_width = $(this.element[0]).dialog("option", "width");
        this.original_size_maxHeight = $(this.element[0]).dialog("option", "maxHeight");
        this.original_position_mode = $(this.element[0]).dialog("widget").css("position");
        this.original_position_left = $(this.element[0]).dialog("widget").offset().left - $('body').scrollLeft();
        this.original_position_top = $(this.element[0]).dialog("widget").offset().top - $('body').scrollTop();
        return this.original_titlebar_wrap = $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar").css("white-space");
      }
    },
    _loadSnapshot: function() {
      return {
        "config": {
          "resizable": this.original_config_resizable,
          "draggable": this.original_config_draggable
        },
        "size": {
          "height": this.original_size_height,
          "width": this.original_size_width,
          "maxHeight": this.original_size_maxHeight
        },
        "position": {
          "mode": this.original_position_mode,
          "left": this.original_position_left,
          "top": this.original_position_top
        },
        "titlebar": {
          "wrap": this.original_titlebar_wrap
        }
      };
    },
    _toggleButtons: function(newstate) {
      var mode, name, state, _ref, _ref1, _results;

      state = newstate || this._state;
      $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar-restore").toggle(state !== "normal").css({
        "right": "1.4em"
      }).end();
      _ref = this.modes;
      for (name in _ref) {
        mode = _ref[name];
        $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar-" + name).toggle(state !== mode.state && this.options[mode.option]);
      }
      _ref1 = this.modes;
      _results = [];
      for (name in _ref1) {
        mode = _ref1[name];
        if (mode.state === state) {
          _results.push($(this.element[0]).dialog("widget").find(".ui-dialog-titlebar-restore").insertAfter($(this.element[0]).dialog("widget").find(".ui-dialog-titlebar-" + name)).end());
        } else {
          _results.push(void 0);
        }
      }
      return _results;
    }
  });

}).call(this);

(function() {
  var $;

  $ = jQuery;

  $.extend(true, $.ui.dialogExtend.prototype, {
    modes: {
      "collapse": {
        option: "collapsable",
        state: "collapsed"
      }
    },
    options: {
      "collapsable": false,
      "icons": {
        "collapse": "ui-icon-triangle-1-s"
      },
      "beforeCollapse": null,
      "collapse": null
    },
    collapse: function() {
      var newHeight, pos;

      newHeight = $(this.element[0]).dialog("widget").find(".ui-dialog-titlebar").height() + 2;
      this._trigger("beforeCollapse");
      if (this._state !== "normal") {
        this._restore();
      }
      this._saveSnapshot();
      pos = $(this.element[0]).dialog("widget").position();
      $(this.element[0]).dialog("option", {
        "resizable": false,
        "height": newHeight,
        "maxHeight": newHeight,
        "position": [pos.left - $(document).scrollLeft(), pos.top - $(document).scrollTop()]
      }).on('dialogclose', this._collapse_restore).dialog("widget").find(".ui-dialog-buttonpane:visible").hide().end().find(".ui-dialog-titlebar").css("white-space", "nowrap").end().find(".ui-dialog-content");
      this._setState("collapsed");
      this._toggleButtons();
      return this._trigger("collapse");
    },
    _restore_collapsed: function() {
      var original;

      original = this._loadSnapshot();
      return $(this.element[0]).show().dialog("widget").find(".ui-dialog-buttonpane:hidden").show().end().find(".ui-dialog-titlebar").css("white-space", original.titlebar.wrap).end().find(".ui-dialog-content").dialog("option", {
        "resizable": original.config.resizable,
        "height": original.size.height,
        "maxHeight": original.size.maxHeight
      }).off('dialogclose', this._collapse_restore);
    },
    _initStyles_collapse: function() {
      var style;

      if (!$(".dialog-extend-collapse-css").length) {
        style = '';
        style += '<style class="dialog-extend-collapse-css" type="text/css">';
        style += '.ui-dialog .ui-dialog-titlebar-collapse { width: 19px; height: 18px; }';
        style += '.ui-dialog .ui-dialog-titlebar-collapse span { display: block; margin: 1px; }';
        style += '.ui-dialog .ui-dialog-titlebar-collapse:hover,';
        style += '.ui-dialog .ui-dialog-titlebar-collapse:focus { padding: 0; }';
        style += '</style>';
        return $(style).appendTo("body");
      }
    },
    _collapse_restore: function() {
      return $(this).dialogExtend("restore");
    }
  });

}).call(this);
