from BeautifulSoup import BeautifulSoup, Tag

import itertools
import urllib
import urllib2
import json


URL = 'http://islandslo.bbstats.pointstreak.com/ajax/teams_ajax.php'

QUERY_PARAMS = {
    'teamid'    : 38484,
    'seasonid'  : 12236,
    'action'    : 'reorderbat',
    'orderby'   : 'avg',
    'direction' : 'DESC',
    }

def group(xs, n):
  return itertools.izip(*[itertools.islice(xs, i, None, n) for i in range(n)])

if __name__ == '__main__':
  document = urllib2.urlopen('%s?%s' % (URL, urllib.urlencode(QUERY_PARAMS))).read()
  html = BeautifulSoup(json.loads(document)['html'])

  cols = [a for th in html.findAll('th') for a in th.find('a')]
  rows = group(
      [td.text for tds in [tr.findAll('td') for tr in html.findAll('tr')] for td in tds if len(tds) != 0], len(cols)
      )

  players = []
  stats = ['G', 'AB', 'H', '2B', '3B', 'HR', 'RBI', 'R', 'BB', 'SO', 'SF', 'E']

  for (i, row) in enumerate(rows):
    players.append({'stats':{}})

    for (j, col) in enumerate(row):
      key = cols[j]

      if key == 'Player':
        players[i]['name'] = col

      elif key in stats:
        players[i]['stats'][key] = int(col) if col else 0

  print json.dumps(players)








