package com.example.rickandmortycompose.presentation.charactersList

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.rickandmortycompose.R
import com.example.rickandmortycompose.data.characters.ResultCharacterDto
import com.example.rickandmortycompose.presentation.GlideImageWithPreview
import com.example.rickandmortycompose.presentation.RaMViewModel
import com.example.rickandmortycompose.presentation.navigation.Screen
import com.example.rickandmortycompose.ui.theme.backgroundCharacter
import com.example.rickandmortycompose.ui.theme.secondaryText

@Composable
fun CharacterList(
    context: Context, navController: NavController, viewModel: RaMViewModel
) {
    val pagingData by lazy { CharactersListPagingSource.page(viewModel) }
    val characters: LazyPagingItems<ResultCharacterDto> = pagingData.collectAsLazyPagingItems()

    Column {

        Toolbar(context = context, onClick = { status: String, gender: String ->
            viewModel.setFilterParams(status, gender)
            characters.refresh()
        }, onClearClick = {
            viewModel.setFilterParams("", "")
            characters.refresh()
        })

        CharactersListView(
            viewModel = viewModel, navController = navController, list = characters
        )

    }
}

@Composable
private fun CharactersListView(
    viewModel: RaMViewModel,
    navController: NavController,
    list: LazyPagingItems<ResultCharacterDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(list) {
            it?.let {
                ItemCharacter(
                    viewModel = viewModel,
                    navController = navController,
                    character = it
                )
            }
        }
        list.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadStatus()
                        }
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = modifier.fillParentMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadStatus()
                        }
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = list.loadState.refresh as LoadState.Error
                    item {
                        Column(
                            modifier = modifier.fillParentMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            e.error.localizedMessage?.let { Text(text = it) }
                            Button(onClick = { retry() }, colors = buttonColors(Color.LightGray)) {
                                Text(text = stringResource(id = R.string.reload))
                            }
                        }
                    }
                }
                loadState.append is LoadState.Error -> {
                    val e = list.loadState.append as LoadState.Error
                    item {
                        Column(
                            modifier = modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            e.error.localizedMessage?.let { Text(text = it) }
                            Button(onClick = { retry() }, colors = buttonColors(Color.LightGray)) {
                                Text(text = stringResource(id = R.string.reload))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadStatus() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(modifier = Modifier.size(100.dp), color = Color.Gray)
    }
}


@Composable
fun Toolbar(
    context: Context,
    onClick: (String, String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(backgroundColor = backgroundCharacter) {

        IconButton(onClick = { Toast.makeText(context, "See later", Toast.LENGTH_SHORT).show() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = R.string.btn_search_title)
            )
        }

        Text(
            text = stringResource(id = R.string.character_list_toolbar), modifier = modifier.padding(start = 4.dp)
        )
        Spacer(modifier.weight(1f, true))


        var openDialogValue by remember { mutableStateOf(false) }
        IconButton(onClick = {
            openDialogValue = !openDialogValue
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = stringResource(id = R.string.btn_filter_title)
            )
        }
        if (openDialogValue) {
            FilterDialog(
                context = context,
                onClick = { status: String, gender: String -> onClick(status, gender) },
                modifier = modifier.background(backgroundCharacter)
            ) { openDialogValue = !openDialogValue }
        }
        IconButton(onClick = { onClearClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cleaning),
                contentDescription = stringResource(id = R.string.btn_clear_filter)
            )
        }
    }
}

@Composable
fun ItemCharacter(
    viewModel: RaMViewModel,
    navController: NavController,
    character: ResultCharacterDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .clickable {
                viewModel.setCharacterForDetail(character)
                navController.navigate(Screen.CharacterDetailScreen.route)
            }
            .padding(5.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier.background(backgroundCharacter)
        ) {

            GlideImageWithPreview(
                data = character.image, modifier = modifier.size(150.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {

                Text(
                    text = character.name,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter =
                        when (character.status?.trim()?.lowercase()) {
                            "alive" -> painterResource(id = R.drawable.ic_alive)
                            "dead" -> painterResource(id = R.drawable.ic_dead)
                            else -> painterResource(id = R.drawable.ic_unknown)
                        },
                        contentDescription = "",
                        modifier = Modifier.size(10.dp)
                    )


                    Text(
                        text = stringResource(
                            id = R.string.status_line,
                            character.status ?: "unknown status",
                            character.species
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp),
                        maxLines = 1,
                    )
                }

                Text(
                    color = secondaryText,
                    text = stringResource(id = R.string.title_last_location),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = character.location?.name ?: "Unknown location",
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun FilterDialog(
    context: Context,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    openDialog: () -> Unit
) {
    Dialog(onDismissRequest = { openDialog() }) {
        var statusShow by remember { mutableStateOf(false) }
        var genderShow by remember { mutableStateOf(false) }
        var statusChecked by remember { mutableStateOf("") }
        var genderChecked by remember { mutableStateOf("") }
        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
                .size(width = 200.dp, height = 550.dp)
                .verticalScroll(rememberScrollState())

        ) {

            // title
            Text(
                text = stringResource(id = R.string.dialog_title),
            )

            // status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(top = 4.dp, start = 8.dp)
            ) {

                Button(
                    onClick = {
                        statusShow = !statusShow
                        if (!statusShow) statusChecked = ""
                    }, modifier = modifier.padding()
                ) {
                    Text(text = stringResource(id = R.string.label_filter_status))
                }

            }
            if (statusShow) {
                val labelAlive = stringResource(id = R.string.label_status_alive)
                val labelDead = stringResource(id = R.string.label_status_dead)
                val labelUnknown = stringResource(id = R.string.label_status_unknown)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp, start = 32.dp)
                ) {
                    RadioButton(selected = statusChecked == labelAlive,
                        onClick = { statusChecked = labelAlive })
                    Text(
                        text = labelAlive, style = MaterialTheme.typography.body1
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp, start = 32.dp)
                ) {
                    RadioButton(selected = statusChecked == labelDead,
                        onClick = { statusChecked = labelDead })
                    Text(
                        text = labelDead, style = MaterialTheme.typography.body1
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp, start = 32.dp)
                ) {
                    RadioButton(selected = statusChecked == labelUnknown,
                        onClick = { statusChecked = labelUnknown })
                    Text(
                        text = labelUnknown,
                        style = MaterialTheme.typography.body1,
                    )
                }
            }

            // filter gender
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(top = 4.dp, start = 8.dp)
            ) {
                Checkbox(checked = genderShow, onCheckedChange = {
                    genderShow = !genderShow
                    if (!genderShow) genderChecked = ""
                })
                Text(
                    text = stringResource(id = R.string.label_filter_gender),
                )
            }
            if (genderShow) {
                val labelMale = stringResource(id = R.string.label_gender_male)
                val labelFemale = stringResource(id = R.string.label_gender_female)
                val labelGenderless = stringResource(id = R.string.label_gender_genderless)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp, start = 32.dp)
                ) {
                    RadioButton(selected = genderChecked == labelMale,
                        onClick = { genderChecked = labelMale })
                    Text(
                        text = labelMale
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp, start = 32.dp)
                ) {
                    RadioButton(selected = genderChecked == labelFemale,
                        onClick = { genderChecked = labelFemale })
                    Text(
                        text = labelFemale,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp, start = 32.dp)
                ) {
                    RadioButton(selected = genderChecked == labelGenderless,
                        onClick = { genderChecked = labelGenderless })
                    Text(
                        text = labelGenderless
                    )
                }
            }
            Spacer(
                modifier.weight(1f, true)
            )
            // save filter
            Button(
                onClick = {
                    val resources = context.resources
                    val status = when (statusChecked) {
                        resources.getString(R.string.label_status_alive) -> resources.getString(R.string.status_alive)
                        resources.getString(R.string.label_status_dead) -> resources.getString(R.string.status_dead)
                        resources.getString(R.string.label_status_unknown) -> resources.getString(R.string.status_unknown)
                        else -> ""
                    }
                    val gender = when (genderChecked) {
                        resources.getString(R.string.label_gender_male) -> resources.getString(R.string.gender_male)

                        resources.getString(R.string.label_gender_female) -> resources.getString(R.string.gender_female)

                        resources.getString(R.string.label_gender_genderless) -> resources.getString(
                            R.string.gender_genderless
                        )
                        else -> ""
                    }
                    onClick(status, gender)
                    openDialog()
                }, modifier = modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_filter_title),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}