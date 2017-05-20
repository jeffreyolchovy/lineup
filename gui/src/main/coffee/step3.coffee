class LineupGui.Step3View extends LineupGui.View

  className: 'step step3'

  template: LineupGui.templates.step3

  events:
    'click .back': 'onBack'

  initialize: ->
    super()

    @form = new LineupGui.Step3Form
    @form.on 'submit', @onSubmit

    return this

  render: ->
    super()

    this.$el.children('.bd').append(@form.el)
    @form.show()
    @fetchLineups()

    return this

  onSubmit: (data) =>
    # no-op

  onBack: =>
    LineupGui.app.navigate 'step2', trigger: true
    window.scrollTo(0, 0)

  fetchLineups: =>
    players = LineupGui.app.players

    specs = _.map LineupGui.app.specs, (spec) ->
      (_.map spec, (data) -> data.state + data.id).join ' '

    data =
      players : players
      strategy:
        initial_population_size   : 2500
        initial_fitness_threshold : 0.5
        maximum_fitness_threshold : 3.5
        maximum_generations       : 100
        fitness_specification     : specs

    $.post '/api/lineups', data, (error, response) =>
      if not error
        @importLineups(response)
      else
        console.log error

  importLineups: (data) =>
    @form.load(data['lineups'])
