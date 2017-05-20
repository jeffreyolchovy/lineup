class LineupGui.FitnessSpec extends LineupGui.View

  tagName: 'li'

  className: 'spec input-wrapper'

  template: LineupGui.templates['fitness-specification']

  constructor: (@data) ->
    super()

  render: ->
    @isRendered = true
    this.$el.html @template(data: @data or {})
    return this
