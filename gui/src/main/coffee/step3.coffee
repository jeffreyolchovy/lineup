class LineupGui.Step3View extends LineupGui.View

  className: 'step step3'

  template: LineupGui.templates.step3

  events:
    'click .back': 'onBack'

  initialize: ->
    super()

    @form = new LineupGui.Step3Form
    @form.on 'submit', @onSubmit

    @spinner = new LineupGui.Spinner text: 'Generating lineups...'

    return this

  render: ->
    super()

    this.$el.children('.bd').append(@form.el)
    @form.show()

    this.$('.button-wrapper').append(@spinner.el)
    @spinner.show(true)

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
      chromosomes : players
      selectors : [
        { weight : 1, type : "top_n", features : { n : 0.1 } }
      ]
      operators : [
        { weight : 3, type : "crossover" },
        { weight : 1, type : "mutation" }
      ]
      fitness_functions : specs

    $.post '/api/lineups', data, (error, response) =>
      @spinner.hide()
      if not error
        @importLineups(response)
      else
        console.log error

  importLineups: (data) =>
    @form.load(data['lineups'])
