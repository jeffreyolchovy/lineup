class LineupGui.Lineup extends LineupGui.View

  className: "lineup"

  template: LineupGui.templates['lineup']

  constructor: (@data) ->
    super()

  render: ->
    @isRendered = true
    this.$el.html @template(data: @data or {})

    for playerName in @data.players
      playerStats = _.find(LineupGui.app.players, (player) -> player.name == playerName)
      playerView = new LineupGui.ReadOnlyPlayer('name': playerName, 'stats': playerStats)
      this.$el.append(playerView.el)
      playerView.show()

    return this
