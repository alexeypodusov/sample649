package ru.alexeypodusov.sample649.data.storage.network.breakingbad

import kotlinx.coroutines.delay
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

class FakeBreakingBadNetworkStorage: BreakingBadNetworkStorage {
    override suspend fun characters(name: String): List<CharacterResponse> {
        delay(1000)
        return listOf(
            CharacterResponse(
                charId = 0,
                name = "Walter White",
                nickname = "Heisenberg",
                img = "https://upload.wikimedia.org/wikipedia/en/0/03/Walter_White_S5B.png"
            ),
            CharacterResponse(
                charId = 1,
                name = "Jesse Pinkman",
                nickname = "Cap n' Cook",
                img = "https://upload.wikimedia.org/wikipedia/en/c/c6/Jesse_Pinkman_S5B.png"
            ),
            CharacterResponse(
                charId = 2,
                name = "Skyler White",
                nickname = "Sky",
                img = "https://upload.wikimedia.org/wikipedia/en/f/fb/Skyler_White_S5B.png"
            ),
            CharacterResponse(
                charId = 3,
                name = "Hank Schrader",
                nickname = "Hank",
                img = "https://upload.wikimedia.org/wikipedia/en/d/db/Hank_Schrader_S5B.png"
            ),
            CharacterResponse(
                charId = 4,
                name = "Walter White Jr.",
                nickname = "Flynn",
                img = "https://upload.wikimedia.org/wikipedia/en/c/ce/Walter_White_Jr_S5B.png"
            ),
            CharacterResponse(
                charId = 5,
                name = "Gustavo Fring",
                nickname = "Gus",
                img = "https://upload.wikimedia.org/wikipedia/en/6/69/Gustavo_Fring_BCS_S3E10.png"
            ),
            CharacterResponse(
                charId = 6,
                name = "James McGill",
                nickname = "Jimmy",
                img = "https://upload.wikimedia.org/wikipedia/en/3/34/Jimmy_McGill_BCS_S3.png"
            ),
            CharacterResponse(
                charId = 7,
                name = "Mike Ehrmantraut",
                nickname = "Mike",
                img = "https://upload.wikimedia.org/wikipedia/en/e/ea/Mike_Ehrmantraut_BCS_S3.png"
            )
        ).filter { name.isEmpty() || it.name.contains(name, true) }
    }
}