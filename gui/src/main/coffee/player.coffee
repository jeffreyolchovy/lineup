class LineupGui.Player extends LineupGui.View

  className: "player player-writable"

  template: LineupGui.templates['player-writable']

  constructor: (@data) ->
    super()

  render: ->
    @isRendered = true
    this.$el.html @template(data: @data or {})
    return this


class LineupGui.ReadOnlyPlayer extends LineupGui.View

  className: "player player-readable"

  template: LineupGui.templates['player-readable']

  constructor: (@data) ->
    super()

  render: ->
    @isRendered = true
    this.$el.html @template(data: @data or {})
    return this
