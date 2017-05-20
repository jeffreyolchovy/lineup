/*
 * Zepto / Underscore Tokenizing Autocomplete
 * jeffreyolchovy, 7/2012
 * 
 * Modified from:
 *
 * Tokenizing Autocomplete Text Entry
 * Version 1.6.0
 *
 * Copyright (c) 2009 James Smith (http://loopj.com)
 * Licensed jointly under the GPL and MIT licenses,
 * choose which one suits your project best!
 *
 */

(function ($, _) {
// Default settings
var DEFAULT_SETTINGS = {
  // Search settings
  method: "GET",
  searchDelay: 300,
  minChars: 1,
  propertyToSearch: "id",
  propertyToAugment: "name",

  // Prepopulation settings
  prePopulate: null,
  processPrePopulate: false,

  // Display settings
  hintText: "Begin typing to filter the list of available statistics",
  noResultsText: "No results",
  searchingText: "Filtering...",
  deleteText: "&times;",
  theme: null,
  zindex: 999,
  resultsLimit: null,

  resultsFormatter: function(item){
    return "<li>" + item[this.propertyToSearch] + " (" + item[this.propertyToAugment] + ")" + "</li>"
  },

  tokenFormatter: function(item) {
    var iconText, iconClassName;

    switch(item.state) {
    case '+':
      iconText = '&gt;'; 
      iconClassName = 'gt';
      break;
    case '-':
      iconText = '&lt;'; 
      iconClassName = 'lt';
      break;
    }

    return "<li class=\"" + iconClassName + "\"><p><span class=\"icon\">" + iconText + "</span><span class=\"text\">" + item[this.propertyToSearch] + "</span></p></li>" },

  // Tokenization settings
  tokenLimit: null,
  tokenDelimiter: ",",
  preventDuplicates: true,
  tokenValue: "id",

  // Other settings
  idPrefix: "token-input-",

  // Keep track if the input is currently in disabled mode
  disabled: false
};

// Default classes to use when theming
var DEFAULT_CLASSES = {
  tokenList: "token-input-list",
  token: "token-input-token",
  tokenReadOnly: "token-input-token-readonly",
  tokenDelete: "token-input-delete-token",
  selectedToken: "token-input-selected-token",
  highlightedToken: "token-input-highlighted-token",
  dropdown: "token-input-dropdown",
  dropdownItem: "token-input-dropdown-item",
  dropdownItem2: "token-input-dropdown-item2",
  selectedDropdownItem: "token-input-selected-dropdown-item",
  inputToken: "token-input-input-token",
  focused: "token-input-focused",
  disabled: "token-input-disabled"
};

// Input box position "enum"
var POSITION = {
  BEFORE: 0,
  AFTER: 1,
  END: 2
};

// Keys "enum"
var KEY = {
  BACKSPACE: 8,
  TAB: 9,
  ENTER: 13,
  ESCAPE: 27,
  SPACE: 32,
  PAGE_UP: 33,
  PAGE_DOWN: 34,
  END: 35,
  HOME: 36,
  LEFT: 37,
  UP: 38,
  RIGHT: 39,
  DOWN: 40,
  NUMPAD_ENTER: 108,
  COMMA: 188
};

var instances = {};

// Additional public (exposed) methods
var methods = {
  init: function(data, options) {
    var settings = $.extend({}, DEFAULT_SETTINGS, options || {});

    return this.each(function() {
      this._id = _.uniqueId();
      instances[this._id] = new $.TokenList(this, data, settings);
    });
  },

  clear: function(id) {
    delete instances[id];
    return this;
  },

  add: function(id, item) {
    instances[id].add(item);
    return this;
  },

  remove: function(id, item) {
    instances[id].remove(item);
    return this;
  },

  get: function(id) {
    return instances[id].getTokens();
  },

  toggleDisabled: function(id, disable) {
    instances[id].toggleDisabled(disable);
    return this;
  }
}

// Expose the .tokenInput function to jQuery as a plugin
$.fn.tokenInput = function(method) {
  // Method calling and initialization logic
  if(methods[method]) {
    return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
  } else {
    return methods.init.apply(this, arguments);
  }
};

// TokenList class for each input
$.TokenList = function(input, data, settings) {
  //
  // Initialization
  //

  // Set the local data to search through
  settings.local_data = data;

  // Build class names
  if(settings.classes) {
    // Use custom class names
    settings.classes = $.extend({}, DEFAULT_CLASSES, settings.classes);
  } else if(settings.theme) {
    // Use theme-suffixed default class names
    settings.classes = {};
    $.each(DEFAULT_CLASSES, function(key, value) {
        settings.classes[key] = value + "-" + settings.theme;
    });
  } else {
    settings.classes = DEFAULT_CLASSES;
  }

  // Save the tokens
  var saved_tokens = [];

  // Keep track of the number of tokens in the list
  var token_count = 0;

  // Basic cache to save on db hits
  var cache = new $.TokenList.Cache();

  // Keep track of the timeout, old vals
  var timeout;
  var input_val;

  // Create a new text input an attach keyup events
  var input_box = $("<input type=\"text\"  autocomplete=\"off\">")
    .css({outline: "none"})
    .attr("id", settings.idPrefix + input.id)
    .focus(function() {
      if (settings.disabled) {
        return false;
      } else
      if (settings.tokenLimit === null || settings.tokenLimit !== token_count) {
        show_dropdown_hint();
      }
      token_list.addClass(settings.classes.focused);
    })
    .blur(function() {
      hide_dropdown();
      $(this).val("");
      token_list.removeClass(settings.classes.focused);
    })
    .bind("keyup keydown blur update", resize_input)
    .keydown(function (event) {
      var previous_token;
      var next_token;

      switch(event.keyCode) {
        case KEY.LEFT:
        case KEY.RIGHT:
        case KEY.UP:
        case KEY.DOWN:
          if(!$(this).val()) {
            previous_token = input_token.prev();
            next_token = input_token.next();

            if((previous_token.length && previous_token.get(0) === selected_token) || (next_token.length && next_token.get(0) === selected_token)) {
              // Check if there is a previous/next token and it is selected
              if(event.keyCode === KEY.LEFT || event.keyCode === KEY.UP) {
                deselect_token($(selected_token), POSITION.BEFORE);
              } else {
                deselect_token($(selected_token), POSITION.AFTER);
              }
            } else if((event.keyCode === KEY.LEFT || event.keyCode === KEY.UP) && previous_token.length) {
              // We are moving left, select the previous token if it exists
              select_token($(previous_token.get(0)));
            } else if((event.keyCode === KEY.RIGHT || event.keyCode === KEY.DOWN) && next_token.length) {
              // We are moving right, select the next token if it exists
              select_token($(next_token.get(0)));
            }
          } else {
            var dropdown_item = null;

            if(event.keyCode === KEY.DOWN || event.keyCode === KEY.RIGHT) {
              dropdown_item = $(selected_dropdown_item).next();
            } else {
              dropdown_item = $(selected_dropdown_item).prev();
            }

            if(dropdown_item.length) {
              select_dropdown_item(dropdown_item);
            }
          }
          return false;
          break;

          case KEY.BACKSPACE:
            previous_token = input_token.prev();

            if(!$(this).val().length) {
              if(selected_token) {
                delete_token($(selected_token));
                hidden_input.change();
              } else if(previous_token.length) {
                select_token($(previous_token.get(0)));
              }

              return false;
            } else if($(this).val().length === 1) {
              hide_dropdown();
            } else {
              // set a timeout just long enough to let this function finish.
              setTimeout(function(){do_search();}, 5);
            }
            break;

            case KEY.TAB:
            case KEY.ENTER:
            case KEY.NUMPAD_ENTER:
            case KEY.COMMA:
              if(selected_dropdown_item) {
                add_token(JSON.parse($(selected_dropdown_item).data("tokeninput")));
                hidden_input.change();
                return false;
              }
              break;

            case KEY.ESCAPE:
              hide_dropdown();
              return true;

            default:
              if(String.fromCharCode(event.which)) {
                // set a timeout just long enough to let this function finish.
                setTimeout(function(){do_search();}, 5);
              }
              break;
          }
      });

  // Keep a reference to the original input box
  var hidden_input = $(input)
   .hide()
   .val("")
   .focus(function () {
       focus_with_timeout(input_box);
   })
   .blur(function () {
       input_box.blur();
   });

  // Keep a reference to the selected token and dropdown item
  var selected_token = null;
  var selected_token_index = 0;
  var selected_dropdown_item = null;

  // The list to store the token items in
  var token_list = $("<ul />")
    .addClass(settings.classes.tokenList)
    .click(function (event) {
      var li = $(event.target).closest("li");
      // Deselect selected token
      if(selected_token) {
          deselect_token($(selected_token), POSITION.END);
      }

      // Focus input box
      focus_with_timeout(input_box);
    })
    .mouseover(function (event) {
      var li = $(event.target).closest("li");
      if(li && selected_token !== this) {
        li.addClass(settings.classes.highlightedToken);
      }
    })
    .mouseout(function (event) {
      var li = $(event.target).closest("li");
      if(li && selected_token !== this) {
        li.removeClass(settings.classes.highlightedToken);
      }
    })
    .insertBefore(hidden_input);

  // The token holding the input box
  var input_token = $("<li />")
      .addClass(settings.classes.inputToken)
      .appendTo(token_list)
      .append(input_box);

  // The list to store the dropdown items in
  var dropdown = $("<div>")
      .addClass(settings.classes.dropdown)
      .appendTo("body")
      .hide();

  // Magic element to help us resize the text input
  var input_resizer = $("<tester/>")
    .insertAfter(input_box)
    .css({
        position: "absolute",
        top: -9999,
        left: -9999,
        width: "auto",
        whiteSpace: "nowrap"
    });

  // Pre-populate list if items exist
  hidden_input.val("");

  var li_data = settings.prePopulate;

  if(li_data && li_data.length) {
    $.each(li_data, function(index, value) {
      insert_token(value);
      checkTokenLimit();
    });
  }

  // Check if widget should initialize as disabled
  if (settings.disabled) {
    toggleDisabled(true);
  }

  //
  // Public functions
  //

  this.clear = function() {
    token_list.children("li").each(function() {
      if($(this).children("input").length === 0) delete_token($(this));
    });
  }

  this.add = function(item) {
    add_token(item);
  }

  this.remove = function(item) {
    token_list.children("li").each(function() {
      if($(this).children("input").length === 0) {
        var currToken = JSON.parse($(this).data("tokeninput"));
        var match = true;

        for(var prop in item) {
          if(item[prop] !== currToken[prop]) {
            match = false;
            break;
          }
        }

        if(match) delete_token($(this));
      }
    });
  }

  this.getTokens = function() {
    var tokens = [];

    token_list.children(".token-input-token").each(function() {
      var data = $(this).data("tokeninput");
      tokens.push(JSON.parse(data));
    });

    return tokens;
  }

  this.toggleDisabled = function(disable) {
    toggleDisabled(disable);
  }

  //
  // Private functions
  //

  // Toggles the widget between enabled and disabled state, or according
  // to the [disable] parameter.
  function toggleDisabled(disable) {
    if(typeof disable === 'boolean') {
      settings.disabled = disable
    } else {
      settings.disabled = !settings.disabled;
    }

    input_box.prop('disabled', settings.disabled);
    token_list.toggleClass(settings.classes.disabled, settings.disabled);

    // if there is any token selected we deselect it
    if(selected_token) {
      deselect_token($(selected_token), POSITION.END);
    }

    hidden_input.prop('disabled', settings.disabled);
  }

  function checkTokenLimit() {
    if(settings.tokenLimit !== null && token_count >= settings.tokenLimit) {
      input_box.hide();
      hide_dropdown();
      return;
    }
  }

  function resize_input() {
    if(input_val === (input_val = input_box.val())) return;

    // Enter new content into resizer and resize input accordingly
    var escaped = input_val.replace(/&/g, '&amp;').replace(/\s/g,' ').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    input_resizer.html(escaped);
    input_box.width(input_resizer.width() + 15);
  }

  function is_printable_character(keycode) {
    return ((keycode >= 48 && keycode <= 90) ||     // 0-1a-z
            (keycode >= 96 && keycode <= 111) ||    // numpad 0-9 + - / * .
            (keycode >= 186 && keycode <= 192) ||   // ; = , - . / ^
            (keycode >= 219 && keycode <= 222));    // ( \ ) '
  }

  // Inner function to a token to the list
  function insert_token(item) {
    function change_state(token, state) {
      var data = JSON.parse(token.data("tokeninput"));
      data.state = state;
      token.data("tokeninput", JSON.stringify(data));
    }

    var $this_token = $(settings.tokenFormatter(item));

    $this_token.click(function(e) {
      e.preventDefault();
      e.stopPropagation();

      if($(this).hasClass('gt')) {
        $(this).removeClass('gt').addClass('lt');
        $('span:first-child', $(this)).html('&lt;');
        change_state($($this_token.get(0)), '-');
      } else if($(this).hasClass('lt')) {
        $(this).removeClass('lt').addClass('gt');
        $('span:first-child', $(this)).html('&gt;');
        change_state($($this_token.get(0)), '+');
      }
    });

    $this_token.addClass(settings.classes.token).insertBefore(input_token);

    // The 'delete token' button
    $("<span>" + settings.deleteText + "</span>")
        .addClass(settings.classes.tokenDelete)
        .addClass("close")
        .appendTo($this_token)
        .click(function() {
            if (!settings.disabled) {
                delete_token($(this).parent());
                hidden_input.change();
                return false;
            }
        });

    // Store data on the token
    var token_data = item;
    $($this_token.get(0)).data("tokeninput", JSON.stringify(item));

    // Save this token for duplicate checking
    saved_tokens = saved_tokens.slice(0,selected_token_index)
                               .concat([token_data])
                               .concat(saved_tokens
                               .slice(selected_token_index));

    selected_token_index++;

    // Update the hidden input
    update_hidden_input(saved_tokens, hidden_input);

    token_count += 1;

    // Check the token limit
    if(settings.tokenLimit !== null && token_count >= settings.tokenLimit) {
      input_box.hide();
      hide_dropdown();
    }

    return $this_token;
  }

  // Add a token to the token list based on user input
  function add_token(item) {
    // See if the token already exists and select it if we don't want duplicates
    if(token_count > 0 && settings.preventDuplicates) {
      var found_existing_token = null;

      token_list.children().each(function() {
        var existing_token = $(this);
        var token_data = $(existing_token.get(0)).data("tokeninput");

        if(token_data) {
          var existing_data = JSON.parse($(existing_token.get(0)).data("tokeninput"));

          if(existing_data && existing_data.id === item.id) {
            found_existing_token = existing_token;
            return false;
          }
        }
      });

      if(found_existing_token) {
        select_token(found_existing_token);
        input_token.insertAfter(found_existing_token);
        focus_with_timeout(input_box);
        return;
      }
    }

    // Insert the new tokens
    if(settings.tokenLimit == null || token_count < settings.tokenLimit) {
      insert_token(item);
      checkTokenLimit();
    }

    // Clear input box
    input_box.val("");

    // Don't show the help dropdown, they've got the idea
    hide_dropdown();
  }

  // Select a token in the token list
  function select_token(token) {
    if(!settings.disabled) {
      token.addClass(settings.classes.selectedToken);
      selected_token = token.get(0);

      // Hide input box
      input_box.val("");

      // Hide dropdown if it is visible (eg if we clicked to select token)
      hide_dropdown();
    }
  }

  // Deselect a token in the token list
  function deselect_token(token, position) {
    token.removeClass(settings.classes.selectedToken);
    selected_token = null;

    if(position === POSITION.BEFORE) {
      input_token.insertBefore(token);
      selected_token_index--;
    } else if(position === POSITION.AFTER) {
      input_token.insertAfter(token);
      selected_token_index++;
    } else {
      input_token.appendTo(token_list);
      selected_token_index = token_count;
    }

    // Show the input box and give it focus again
    focus_with_timeout(input_box);
  }

  // Toggle selection of a token in the token list
  function toggle_select_token(token) {
    var previous_selected_token = selected_token;

    if(selected_token) {
      deselect_token($(selected_token), POSITION.END);
    }

    if(previous_selected_token === token.get(0)) {
      deselect_token(token, POSITION.END);
    } else {
      select_token(token);
    }
  }

  function dir(elem, dir, until) {
    var matched = [], cur = elem[dir];

    while(cur && cur.nodeType !== 9 && (until === undefined || cur.nodeType !== 1 || !$(cur).is(until))) {
      if(cur.nodeType === 1) matched.push(cur);
      cur = cur[dir];
    }

    return matched;
  }

  // Delete a token from the token list
  function delete_token(token) {
    // Remove the id from the saved list
    var index = dir(token, "previousSibling").length;

    if(index > selected_token_index) index--;

    // Delete the token
    token.remove();
    selected_token = null;

    // Show the input box and give it focus again
    focus_with_timeout(input_box);

    // Remove this token from the saved list
    saved_tokens = saved_tokens.slice(0,index).concat(saved_tokens.slice(index+1));
    if(index < selected_token_index) selected_token_index--;

    // Update the hidden input
    update_hidden_input(saved_tokens, hidden_input);

    token_count -= 1;

    if(settings.tokenLimit !== null) {
      input_box.show().val("");
      focus_with_timeout(input_box);
    }
  }

  // Update the hidden input box value
  function update_hidden_input(saved_tokens, hidden_input) {
    var token_values = $.map(saved_tokens, function(el) {
      if(typeof settings.tokenValue == 'function')
        return settings.tokenValue.call(this, el);

      return el[settings.tokenValue];
    });

    hidden_input.val(token_values.join(settings.tokenDelimiter));
  }

  // Hide and clear the results dropdown
  function hide_dropdown() {
    dropdown.hide().empty();
    selected_dropdown_item = null;
  }

  function show_dropdown() {
    dropdown.css({
      position: "absolute",
      top: $(token_list).offset().top + $(token_list).height(),
      left: $(token_list).offset().left + 1,
      width: $(token_list).width(),
      'z-index': settings.zindex
    }).show();
  }

  function show_dropdown_searching() {
    if(settings.searchingText) {
      dropdown.html("<p>"+settings.searchingText+"</p>");
      show_dropdown();
    }
  }

  function show_dropdown_hint() {
    if(settings.hintText) {
      dropdown.html("<p>"+settings.hintText+"</p>");
      show_dropdown();
    }
  }

  var regexp_special_chars = new RegExp('[.\\\\+*?\\[\\^\\]$(){}=!<>|:\\-]', 'g');

  function regexp_escape(term) {
    return term.replace(regexp_special_chars, '\\$&');
  }

  // Highlight the query part of the search term
  function highlight_term(value, term) {
    return value.replace(
      new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + regexp_escape(term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"),
    "<b>$1</b>");
  }

  function find_value_and_highlight_term(template, value, term) {
    return template.replace(
      new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + regexp_escape(value) + ")(?![^<>]*>)(?![^&;]+;)", "g"),
    highlight_term(value, term));
  }

  // Populate the results dropdown with some results
  function populate_dropdown(query, results) {
    if(results && results.length) {
      dropdown.empty();

      var dropdown_ul = $("<ul>")
        .appendTo(dropdown)
        .mouseover(function (event) {
            select_dropdown_item($(event.target).closest("li"));
        })
        .mousedown(function (event) {
            add_token(JSON.parse($(event.target).closest("li").data("tokeninput")));
            hidden_input.change();
            return false;
        })
        .hide();

      if (settings.resultsLimit && results.length > settings.resultsLimit) {
        results = results.slice(0, settings.resultsLimit);
      }

      $.each(results, function(index, value) {
        var this_li = settings.resultsFormatter(value);
        var x = value[settings.propertyToSearch] + " (" + value[settings.propertyToAugment] + ")"
        this_li = find_value_and_highlight_term(this_li, x, query);
        this_li = $(this_li).appendTo(dropdown_ul);

        if(index % 2) {
          this_li.addClass(settings.classes.dropdownItem);
        } else {
          this_li.addClass(settings.classes.dropdownItem2);
        }

        if(index === 0) {
          select_dropdown_item(this_li);
        }

        $(this_li.get(0)).data("tokeninput", JSON.stringify(value));
      });

      show_dropdown();
      dropdown_ul.show();
    } else {
      if(settings.noResultsText) {
        dropdown.html("<p>"+settings.noResultsText+"</p>");
        show_dropdown();
      }
    }
  }

  // Highlight an item in the results dropdown
  function select_dropdown_item(item) {
    if(item) {
      if(selected_dropdown_item) {
        deselect_dropdown_item($(selected_dropdown_item));
      }

      item.addClass(settings.classes.selectedDropdownItem);
      selected_dropdown_item = item.get(0);
    }
  }

  // Remove highlighting from an item in the results dropdown
  function deselect_dropdown_item(item) {
    item.removeClass(settings.classes.selectedDropdownItem);
    selected_dropdown_item = null;
  }

  // Do a search and show the "searching" dropdown if the input is longer
  // than settings.minChars
  function do_search() {
    var query = input_box.val();

    if(query && query.length) {
      if(selected_token) {
        deselect_token($(selected_token), POSITION.AFTER);
      }

      if(query.length >= settings.minChars) {
        show_dropdown_searching();
        clearTimeout(timeout);

        timeout = setTimeout(function() { run_search(query); }, settings.searchDelay);
      } else {
        hide_dropdown();
      }
    }
  }

  // Do the actual search
  function run_search(query) {
    var cached_results = cache.get(query);

    if(cached_results) {
      populate_dropdown(query, cached_results);
    } else {
      var results = _.filter(settings.local_data, function (row) {
        return row[settings.propertyToSearch].toLowerCase().indexOf(query.toLowerCase()) > -1;
      });

      results = results.concat(_.filter(settings.local_data, function (row) {
        var isResult = results.indexOf(row) > -1
        return !isResult && row[settings.propertyToAugment].toLowerCase().indexOf(query.toLowerCase()) > -1;
      }));

      cache.add(query, results);
      populate_dropdown(query, results);
    }
  }

  // Bring browser focus to the specified object.
  // Use of setTimeout is to get around an IE bug.
  // (See, e.g., http://stackoverflow.com/questions/2600186/focus-doesnt-work-in-ie)
  //
  // obj: a jQuery object to focus()
  function focus_with_timeout(obj) {
    setTimeout(function() { obj.focus(); }, 50);
  }
};

$.TokenList.Cache = function(options) {
  var settings = $.extend({max_size: 500}, options);
  var data = {};
  var size = 0;

  var flush = function() {
    data = {};
    size = 0;
  };

  this.add = function(query, results) {
    if(size > settings.max_size) flush();
    if(!data[query]) size += 1;
    data[query] = results;
  };

  this.get = function (query) {
    return data[query];
  };
};
}(Zepto, _));
