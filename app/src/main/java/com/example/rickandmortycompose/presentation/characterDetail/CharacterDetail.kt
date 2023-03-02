package com.example.rickandmortycompose.presentation.characterDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rickandmortycompose.R
import com.example.rickandmortycompose.data.EpisodeDto
import com.example.rickandmortycompose.data.characters.ResultCharacterDto
import com.example.rickandmortycompose.entity.Episode
import com.example.rickandmortycompose.entity.characters.Character
import com.example.rickandmortycompose.presentation.GlideImageWithPreview
import com.example.rickandmortycompose.presentation.RaMViewModel
import com.example.rickandmortycompose.presentation.navigation.Screen
import com.example.rickandmortycompose.ui.theme.backgroundCharacter
import com.example.rickandmortycompose.ui.theme.backgroundList
import com.example.rickandmortycompose.ui.theme.primaryText
import com.example.rickandmortycompose.ui.theme.secondaryText

@Composable
fun CharacterDetail(
    navController: NavController, viewModel: RaMViewModel
) {
    val episodes: List<EpisodeDto> by viewModel.episodes.collectAsState(initial = emptyList())
    val character = viewModel.getCharacterForDetail()
    viewModel.loadEpisodes(character)



    Column(modifier = Modifier.background(backgroundList).fillMaxSize()) {
        Toolbar(
            onClick = { navController.navigate(Screen.CharacterListScreen.route) },
        )
        CharacterInfoView(character = ResultCharacterDto(
            created = character.created,
            episode = character.episode,
            gender = character.gender,
            id = character.id,
            image = character.image,
            location = character.location,
            name = character.name,
            origin = character.origin,
            species = character.species,
            status = character.status,
            type = character.type,
            url = character.url
        ), loadEpisodes = { episodes })
    }
}


@Composable
fun CharacterInfoView(
    character: Character,
    loadEpisodes: @Composable () -> List<Episode>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(backgroundList)
            .verticalScroll(rememberScrollState())
    ) {

        //avatar
        GlideImageWithPreview(
            data = character.image,
            modifier = modifier
                .fillMaxWidth()
                .size(200.dp),
            contentScale = ContentScale.Crop
        )

        //name
        Text(
            text = character.name,
            modifier = modifier.fillMaxWidth(),
            color = primaryText,
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
        )
        Column(
            modifier = modifier
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {

                //status
                Text(
                    text = stringResource(id = R.string.title_status),
                    color = secondaryText,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        painter = when (character.status?.trim()?.lowercase()) {
                            "alive" -> painterResource(id = R.drawable.ic_alive)
                            "dead" -> painterResource(id = R.drawable.ic_dead)
                            else -> painterResource(id = R.drawable.ic_unknown)
                        }, contentDescription = "", modifier = Modifier.size(10.dp)
                    )

                    Text(
                        text = character.status ?: "Unknown",
                        color = primaryText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                }
            }

            CurrentParamView(
                title = stringResource(id = R.string.title_species_gender),
                text = "${character.species} (${character.gender ?: "Unknown"})"
            )
            CurrentParamView(
                title = stringResource(id = R.string.title_last_location),
                text = character.location?.name ?: "Unknown"
            )
            CurrentParamView(
                title = stringResource(id = R.string.title_first_seen),
                text = character.episode.first()
            )

            Text(
                text = stringResource(id = R.string.title_episodes),
                modifier = Modifier.padding(top = 16.dp, start = 8.dp), fontSize = 20.sp
            )
        }

        val episodes = loadEpisodes()
        episodes.forEach {
            EpisodeItem(episode = it)
        }
    }
}

@Composable
fun CurrentParamView(title: String, text: String) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = title, color = secondaryText, modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = text, color = primaryText, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Toolbar(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    TopAppBar(backgroundColor = backgroundCharacter) {
        IconButton(onClick = { onClick() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack, contentDescription = null
            )
        }
        Text(
            text = stringResource(id = R.string.character_detail_toolbar),
            modifier = modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun EpisodeItem(episode: Episode, modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(backgroundCharacter)
    ) {
        Column(
            modifier = modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = episode.name,
                modifier = modifier.padding(top = 4.dp, bottom = 4.dp),
                maxLines = 1
            )
            Text(
                text = episode.airDate, modifier = modifier.padding(vertical = 4.dp)
            )
        }
        Text(
            text = episode.episode, modifier = modifier.padding(end = 8.dp, top = 4.dp)
        )
    }
}