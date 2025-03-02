package github

import sttp.client4.quick._
import upickle.default._
import sttp.model.Method
import sttp.model.Uri

val GITHUB_API_URL = "https://api.github.com"

private def githubRequest(method: Method, path: Uri) = {
  basicRequest
    .method(method, path)
    .header("Authorization", s"token ${sys.env("GITHUB_TOKEN")}")
}

object RepoRepository {
  def findByUsername(username: String) = {
    var response =
      githubRequest(Method.GET, uri"${GITHUB_API_URL}/users/${username}/repos")
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"user(${username}) repos data not found: ${e}")
        "[]"
      }
    }

    read[List[Models.Repo]](body)
  }

  def findByTeam(login: String, slug: String) = {
    var response =
      githubRequest(
        Method.GET,
        uri"${GITHUB_API_URL}/orgs/${login}/teams/${slug}/repos"
      )
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(
          s"team(${login}, ${slug}) repos data not found: ${e}"
        )
        "[]"
      }
    }

    read[List[Models.Repo]](body)
  }
}

object OrganizationRepository {
  def findByUsername(username: String) = {
    var response =
      githubRequest(Method.GET, uri"${GITHUB_API_URL}/users/${username}/orgs")
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"user(${username}) orgs data not found: ${e}")
        "[]"
      }
    }

    read[List[Models.Organization]](body)
  }
}

object TeamRepository {
  def findByOrganization(login: String) = {
    var response =
      githubRequest(Method.GET, uri"${GITHUB_API_URL}/orgs/${login}/teams")
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"org(${login}) teams data not found: ${e}")
        "[]"
      }
    }

    read[List[Models.Team]](body)
  }
}

object UserRepository {
  def findByTeam(login: String, slug: String) = {
    var response =
      githubRequest(
        Method.GET,
        uri"${GITHUB_API_URL}/orgs/${login}/teams/${slug}/members"
      )
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(
          s"team(${login}, ${slug}) members data not found: ${e}"
        )
        "[]"
      }
    }

    read[List[Models.User]](body)
  }
}

object PullRepository {
  def findByFullName(owner: String, name: String) = {
    var response =
      githubRequest(
        Method.GET,
        uri"${GITHUB_API_URL}/repos/${owner}/${name}/pulls"
      )
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(
          s"target(${owner}, ${name}) pulls data not found: ${e}"
        )
        "[]"
      }
    }

    read[List[Models.Pull]](body)
  }
}
