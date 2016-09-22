#!/usr/bin/env python
import sys
import os
import os.path
import xml.dom.minidom

if os.environ["TRAVIS_SECURE_ENV_VARS"] == "false":
  print "no secure env vars available, skipping deployment"
  sys.exit()

homedir = os.path.expanduser("~")

m2 = xml.dom.minidom.parse(homedir + '/.m2/settings.xml')
settings = m2.getElementsByTagName("settings")[0]

serversNodes = settings.getElementsByTagName("servers")
if not serversNodes:
  serversNode = m2.createElement("servers")
  settings.appendChild(serversNode)
else:
  serversNode = serversNodes[0]

snapshotsServerNode = m2.createElement("server")
snapshotsServerId = m2.createElement("id")
snapshotsServerUser = m2.createElement("username")
snapshotsServerPass = m2.createElement("password")

snapshotsIdNode = m2.createTextNode("sonatype-nexus-snapshots")
snapshotsUserNode = m2.createTextNode(os.environ["SONATYPE_USERNAME"])
snapshotsPassNode = m2.createTextNode(os.environ["SONATYPE_PASSWORD"])

snapshotsServerId.appendChild(snapshotsIdNode)
snapshotsServerUser.appendChild(snapshotsUserNode)
snapshotsServerPass.appendChild(snapshotsPassNode)

snapshotsServerNode.appendChild(snapshotsServerId)
snapshotsServerNode.appendChild(snapshotsServerUser)
snapshotsServerNode.appendChild(snapshotsServerPass)

serversNode.appendChild(snapshotsServerNode)

stagingServerNode = m2.createElement("server")
stagingServerId = m2.createElement("id")
stagingServerUser = m2.createElement("username")
stagingServerPass = m2.createElement("password")

stagingIdNode = m2.createTextNode("sonatype-nexus-staging")
stagingUserNode = m2.createTextNode(os.environ["SONATYPE_USERNAME"])
stagingPassNode = m2.createTextNode(os.environ["SONATYPE_PASSWORD"])

stagingServerId.appendChild(stagingIdNode)
stagingServerUser.appendChild(stagingUserNode)
stagingServerPass.appendChild(stagingPassNode)

stagingServerNode.appendChild(stagingServerId)
stagingServerNode.appendChild(stagingServerUser)
stagingServerNode.appendChild(stagingServerPass)

serversNode.appendChild(stagingServerNode)

m2Str = m2.toxml()
f = open(homedir + '/.m2/mySettings.xml', 'w')
f.write(m2Str)
f.close()
