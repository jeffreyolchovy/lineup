http =
  onError: (callback) ->
    return (xhr) ->
      json = if xhr.responseText.match(/^\s*$/) then null else JSON.parse(xhr.responseText)
      callback and callback.call(this, xhr.status, json)

  onSuccess: (callback) ->
    return (response) -> callback and callback.call(this, false, response)

  request: (type, url, data, callback) ->
    if $.isFunction data
      callback = data
      data = null

    if type != 'GET'
      data = if !data then '' else JSON.stringify(data)

    $.ajax
      contentType : if type is 'GET' then 'application/x-www-form-urlencoded' else 'application/json'
      dataType    : 'json'
      type        : type
      url         : url
      data        : data
      success     : http.onSuccess(callback)
      error       : http.onError(callback)


$.post = (url, data, callback) -> http.request 'POST', url, data, callback
$.get  = (url, data, callback) -> http.request 'GET',  url, data, callback
