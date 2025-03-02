package github

import upickle.default._

object Usecase {
  def getRepos = {
    val userName = sys.env("GITHUB_USERNAME")

    val userRepos = RepoRepository.findByUsername(userName)

    val orgTeamsMap = OrganizationRepository
      .findByUsername(userName)
      .map({ org =>
        val team = TeamRepository
          .findByOrganization(org.login)
          .filter({ team =>
            UserRepository
              .findByTeam(org.login, team.slug)
              .exists({ user => user.login == userName })
          })

        (org, team)
      })
      .toMap

    val teamRepos =
      orgTeamsMap
        .flatMap({ (org, teams) =>
          teams
            .flatMap({ team =>
              RepoRepository.findByTeam(org.login, team.slug)
            })
        })
        .toList

    val teamSlugs = orgTeamsMap
      .flatMap({ (_, teams) =>
        teams
      })
      .toList
      .map({ team =>
        team.slug
      })

    (userRepos ++ teamRepos, teamSlugs)
  }

  def getAssignPulls = {
    val userName = sys.env("GITHUB_USERNAME")

    val (repos, teamSlugs) = getRepos

    val allPulls = repos
      .flatMap({ repo =>
        PullRepository
          .findByFullName(repo.owner.login, repo.name)
      })

    val assignPulls = allPulls
      .filter({ pull =>
        pull.assignees
          .exists({ user => user.login == userName })
      })

    val reviewerPulls = allPulls
      .filter({ pull =>
        pull.requested_reviewers
          .exists({ user => user.login == userName })
      })

    val teamReviewerPulls = allPulls
      .filter({ pull =>
        pull.requested_teams
          .exists({ team =>
            teamSlugs.exists({ teamSlug => teamSlug == team.slug })
          })
      })

    (assignPulls, reviewerPulls, teamReviewerPulls)
  }
}
