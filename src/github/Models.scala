package github

import upickle.default._

object Models {
  case class Repo(
      name: String = "",
      full_name: String = "",
      owner: User = null,
      pulls_url: String = ""
  ) derives ReadWriter

  case class Organization(
      login: String = "",
      repos_url: String = "",
      avatar_url: String = ""
  ) derives ReadWriter

  case class Team(
      name: String = "",
      slug: String = ""
  ) derives ReadWriter

  case class User(
      login: String = "",
      html_url: String = ""
  ) derives ReadWriter {
    def toSlackLink() = {
      s"<${html_url}|${login}>"
    }
  }

  case class Pull(
      html_url: String = "",
      title: String = "",
      state: String = "",
      user: User = null,
      assignees: List[User] = Nil,
      requested_reviewers: List[User] = Nil,
      requested_teams: List[Team] = Nil,
      base: PullBase = null
  ) derives ReadWriter {
    def toSlackLink() = {
      s"[${base.repo.full_name}] <${html_url}|${title}> (from ${user.toSlackLink()})"
    }
  }

  case class PullBase(
      repo: Repo = null
  ) derives ReadWriter
}
