class LineupGui.App extends Backbone.Router

  initialized = false

  routes:
    ''    : 'step1'
    step1 : 'step1'
    step2 : 'step2'
    step3 : 'step3'

  initialize: ->
    @view = null
    @activeView = null

    # global application data
    @players = []
    @specs   = []

  start: ->
    return if initialized

    # Set default fragment
    window.location.hash = '#step1' unless window.location.hash

    if window.location.hash == '#step2' and @players.length == 0
      window.location.hash = '#step1'

    if window.location.hash == '#step3' and @players.length == 0
      window.location.hash = '#step1'

    if window.location.hash == '#step3' and @specs.length == 0
      window.location.hash = '#step2'

    # Create main application view
    @view = new LineupGui.AppView()

    # Render view
    $('body').append @view.render().el

    initialized = true

    # Start history monitoring
    Backbone.history.start()

    return this

  # Static helper for switching views
  changeView = (i, view) ->
    return ->
      @activeView?.hide()
      @activeView = @view[view].show()
      @view.select(i)

  step1 : changeView 0, 'step1'
  step2 : changeView 1, 'step2'
  step3 : changeView 2, 'step3'


class LineupGui.View extends Backbone.View

  initialize: ->
    @isRendered = false
    return this

  render: ->
    @isRendered = true
    this.$el.html(@template())
    return this

  show: (render = false) ->
    this.render() if render or not @isRendered
    this.$el.show()
    return this

  hide: ->
    this.$el.hide()
    return this


class LineupGui.AppView extends LineupGui.View

  id: 'app'

  template: LineupGui.templates.app

  initialize: ->
    super()

    # Create and hide application subviews
    @step1 = (new LineupGui.Step1View).hide()
    @step2 = (new LineupGui.Step2View).hide()
    @step3 = (new LineupGui.Step3View).hide()

    return this

  render: ->
    super()

    this.$el.children('.bd')
      .append(@step1.el)
      .append(@step2.el)
      .append(@step3.el)

    return this

  select: (i) ->
    this.$('.hd li')
      .removeClass('selected')
      .eq(i)
        .addClass('selected')
