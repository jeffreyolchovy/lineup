class LineupGui.Lineup extends LineupGui.View

  className: "lineup"

  template: LineupGui.templates['lineup']

  constructor: (@data) ->
    super()

  render: ->
    @isRendered = true
    this.$el.html @template(data: @data or {})

    for player in @data.players
      playerView = new LineupGui.ReadOnlyPlayer(player)
      this.$el.append(playerView.el)
      playerView.show()

    return this
