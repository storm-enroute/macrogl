




{
  val publishUser = "SONATYPE_USER"
  val publishPass = "SONATYPE_PASS"
  val userPass = for {
    user <- sys.env.get(publishUser)
    pass <- sys.env.get(publishPass)
  } yield (user, pass)
  val publishCreds: Seq[Setting[_]] = Seq(userPass match {
    case Some((user, pass)) =>
      credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
    case None =>
      // prevent publishing
      publish <<= streams.map(_.log.info("Publishing to Sonatype is disabled since the \"" + publishUser + "\" and/or \"" + publishPass + "\" environment variables are not set."))
  })
  publishCreds
}

