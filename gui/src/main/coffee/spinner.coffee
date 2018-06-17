class LineupGui.Spinner extends LineupGui.View

  tagName: 'div'

  className: 'spinner'

  template: LineupGui.templates['spinner']

  constructor: (@data) ->
    super()

  render: ->
    @isRendered = true
    this.$el.html @template(data: @data or {})
    return this
