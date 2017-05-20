class LineupGui.Step2View extends LineupGui.View

  className: 'step step2'

  template: LineupGui.templates.step2

  events:
    'click .back': 'onBack'

  initialize: ->
    super()

    @form = new LineupGui.Step2Form
    @form.on 'submit', @onSubmit

    return this

  render: ->
    super()

    this.$el.children('.bd').append(@form.el)
    @form.show()

    return this

  onSubmit: (data) =>
    LineupGui.app.specs = data
    LineupGui.app.navigate 'step3', trigger: true
    window.scrollTo(0, 0)

  onBack: =>
    LineupGui.app.navigate 'step1', trigger: true
    window.scrollTo(0, 0)
