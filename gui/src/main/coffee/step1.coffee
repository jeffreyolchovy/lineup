class LineupGui.Step1View extends LineupGui.View

  className: 'step step1'

  template: LineupGui.templates.step1

  events:
    'click #import-roster-trigger': 'promptForTeamId'

  initialize: ->
    super()

    @form = new LineupGui.Step1Form
    @form.on 'submit', @onSubmit

    return this

  render: ->
    super()

    this.$el.children('.bd').append(@form.el)
    @form.show()

    return this

  onSubmit: (data) =>
    players = LineupGui.app.players

    for key of data
      [prefix, i, field] = key.split('_')

      i = (Number) i

      if players.length == i
        players.push({'stats': {}, 'name': undefined})

      if field == 'name'
        players[i].name = data[key] || ('Player ' + (i + 1))
      else
        players[i].stats[field] = Number(data[key])

    valid = _.reduce(players, ((acc, i) -> i.name || acc), null)

    if not valid
      alert("Enter at least one Player Name to proceed")
    else
      LineupGui.app.navigate 'step2', trigger: true
      window.scrollTo(0, 0)

  promptForTeamId: (e) =>
    teamId = prompt('Enter your Island Slowpitch Team ID')

    if teamId? && (not (teamId == ''))
      $.get('/api/import?id=' + teamId, (error, response) => @importPlayers(teamId, response))

  importPlayers: (teamId, data) =>
    if data['players'].length
      @form.load(data['players'])
    else
      alert("Failed to retrieve player data for Team ID #{teamId}")
