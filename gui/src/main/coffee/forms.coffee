class LineupGui.FormView extends LineupGui.View

  tagName: 'form'

  events:
    'submit form': 'onSubmit'

  render: ->
    @isRendered = true
    this.$el.html @template(initial: @initial or {})
    return this

  # Returns a map of form values keyed to the input name
  data: ->
    values = {}
    values[f.name] = f.value for f in this.$el.serializeArray()
    return values

  validate: ->
    return true

  onSubmit: (e) ->
    e.preventDefault()
    data = @data()
    valid = @validate()
    if valid then @trigger 'submit', data


class LineupGui.Step1Form extends LineupGui.FormView

  className: 'form step-form step1-form'
  template: LineupGui.templates['step1-form']

  events:
    'submit form' : 'onSubmit'
    'click .push' : 'onPush'
    'click .pop'  : 'onPop'

  initialize: ->
    super()
    @players = (new LineupGui.Player('index': i) for i in [0..8])
    return this

  render: ->
    super()

    for player in @players
      this.$el.children('.form-inputs').append(player.el)
      player.show()

    return this

  load: (data) =>
    @clear()
    for datum in data
      @push(datum)

  clear: =>
    this.$('.player').remove()
    @players = []

  push: (player) =>
    i = this.$('.player').size()
    @players.push(new LineupGui.Player('index': i, 'name': player.name, 'stats': player.stats))
    this.$el.children('.form-inputs').append(@players[i].el)
    @players[i].show()

  pop: ->
    i = this.$('.player').size()
    if i > 1
      @players.pop(new LineupGui.Player('index': i - 1))
      this.$('.player').last().remove()

  onPush: (e) =>
    @push({})

  onPop: (e) =>
    @pop()


class LineupGui.Step2Form extends LineupGui.FormView

  className: 'form step-form step2-form'

  template: LineupGui.templates['step2-form']

  events:
    'submit form' : 'onSubmit'
    'click .push' : 'onPush'
    'click .pop'  : 'onPop'

  initialize: ->
    super()
    @specs = (new LineupGui.FitnessSpec('index': i) for i in [0..8])
    return this

  render: ->
    super()
    for spec in @specs
      this.$('.specs').append(spec.el)
      spec.show()

    @inputs = this.$('.specs .spec .input > input').tokenInput(@statistics)

    i = 0
    strategies = @strategies
    @inputs.each ->
      id = this._id
      for stat in (strategies[i] || [])
        $(this).tokenInput('add', id, stat)
      i++

    return this

  data: ->
    values = []
    @inputs.each ->
      id = this._id
      values.push($(this).tokenInput('get', id))
    return values

  push: (player) =>
    i = this.$('.spec').size()
    @specs.push(new LineupGui.FitnessSpec('index': i))
    this.$('.specs').append(@specs[i].el)
    @specs[i].show()
    @inputs.push(this.$('.specs .spec .input > input').last().tokenInput(@statistics).get(0))

  pop: () ->
    i = this.$('.spec').size()
    if i > 1
      id = @inputs[i - 1]._id
      $(@inputs[i - 1]).tokenInput('clear', id)
      @inputs = @inputs.slice(0, i - 1)
      @specs.pop(new LineupGui.FitnessSpec('index': i - 1))
      this.$('.spec').last().remove()

  onPush: (e) =>
    @push({})

  onPop: =>
    @pop()

  statistics: [
    {id:"AB",name:"At-Bats",state:"+"}
    {id:"H",name:"Hits",state:"+"}
    {id:"1B",name:"Singles",state:"+"}
    {id:"2B",name:"Doubles",state:"+"}
    {id:"3B",name:"Triples",state:"+"}
    {id:"HR",name:"Homeruns",state:"+"}
    {id:"RBI",name:"Runs Batted-In",state:"+"}
    {id:"R",name:"Runs",state:"+"}
    {id:"BB",name:"Walks",state:"+"}
    {id:"SO",name:"Strike Outs",state:"+"}
    {id:"SF",name:"Sacrifice Fly-Outs",state:"+"}
    {id:"AVG",name:"Batting Average",state:"+"}
    {id:"SLG",name:"Slugging Average",state:"+"}
    {id:"OBA",name:"On-Base Average",state:"+"}
    {id:"ISO",name:"Isolated Power",state:"+"}
    {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
    {id:"HR/H",name:"Homeruns / Hit",state:"+"}
    {id:"BB/AB",name:"Walks / At-Bat",state:"+"}
    {id:"SO/AB",name:"Strike Outs / At-Bat",state:"+"}
    {id:"1B/H",name:"Singles / Hit",state:"+"}
  ]

  strategies: [
    [
      {id:"OBA",name:"On-Base Average",state:"+"}
      {id:"BB/AB",name:"Walks / At-Bat",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"-"}
      {id:"HR/H",name:"Homeruns / Hit",state:"-"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"OBA",name:"On-Base Average",state:"+"}
      {id:"BB/AB",name:"Walks / At-Bat",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"-"}
      {id:"ISO",name:"Isolated Power",state:"-"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
      {id:"BB/AB",name:"Walks / At-Bat",state:"+"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"OBA",name:"On-Base Average",state:"+"}
      {id:"HR/H",name:"Homeruns / Hit",state:"-"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
      {id:"HR/H",name:"Homeruns / Hit",state:"-"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
      {id:"OBA",name:"On-Base Average",state:"+"}
      {id:"SO/AB",name:"Strike Outs / At-Bat",state:"+"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
      {id:"HR/H",name:"Homeruns / Hit",state:"-"}
    ]

    [
      {id:"SLG",name:"Slugging Average",state:"+"}
      {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
      {id:"OBA",name:"On-Base Average",state:"+"}
      {id:"SO/AB",name:"Strike Outs / At-Bat",state:"+"}
    ]

    [
      {id:"PIP",name:"Put-in-Play Percentage",state:"+"}
      {id:"OBA",name:"On-Base Average",state:"-"}
    ]
  ]


class LineupGui.Step3Form extends LineupGui.FormView

  className : 'form step-form step3-form'
  template  : LineupGui.templates['step3-form']

  initialize: ->
    super()
    @lineups = []
    return this

  render: ->
    super()

    for lineup in @lineups
      this.$el.children('.form-inputs').append(lineup.el)
      lineup.show()

    return this

  load: (data) =>
    @clear()
    for datum in data
      @push(datum)

  clear: =>
    this.$('.lineup').remove()
    @lineups = []

  push: (lineup) =>
    i = this.$('.lineup').size()
    @lineups.push(new LineupGui.Lineup('index': i, 'players': lineup))
    this.$el.children('.form-inputs').append(@lineups[i].el)
    @lineups[i].show()
