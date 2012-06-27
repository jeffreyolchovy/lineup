from BeautifulSoup import BeautifulSoup, Tag

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

if __name__ == '__main__':
  document = urllib2.urlopen('%s?%s' % (URL, urllib.urlencode(QUERY_PARAMS))).read()
  html = BeautifulSoup(json.loads(document)['html'])

  print [
      ','.join([a for th in html.findAll('th') for a in th.find('a')]),
      ','.join([td.text.split(',')[0] for tds in [tr.findAll('td') \
          for tr in html.findAll('tr')] for td in tds if len(tds) != 0])
      ]

