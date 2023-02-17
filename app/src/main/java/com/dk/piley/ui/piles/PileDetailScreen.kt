package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.EditableTitleText
import com.dk.piley.ui.theme.PileyTheme
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation

@Composable
fun PileDetailScreen(
    navController: NavController,
    viewModel: PileDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    PileDetailScreen(
        viewState = viewState,
        onDeletePile = {
            navController.popBackStack()
            viewModel.deletePile()
        },
        onEditTitle = { viewModel.editTitle(it) },
        onEditDescription = { viewModel.editDescription(it) },
        onSetPileMode = { viewModel.setPileMode(it) },
        onSetPileLimit = { viewModel.setPileLimit(it) },
        onClose = { navController.popBackStack() }
    )
}

@Composable
fun PileDetailScreen(
    modifier: Modifier = Modifier,
    viewState: PileDetailViewState,
    onDeletePile: () -> Unit = {},
    onEditTitle: (String) -> Unit = {},
    onEditDescription: (String) -> Unit = {},
    onSetPileMode: (PileMode) -> Unit = {},
    onSetPileLimit: (Int) -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        "close the task detail",
                        modifier = Modifier.scale(
                            1.5F
                        ),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                EditableTitleText(
                    viewState.titleTextValue, viewState.canDeleteOrEdit, onEditTitle
                )
                IconButton(onClick = { }, enabled = false) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        "close the task detail",
                        modifier = Modifier.scale(
                            1.5F
                        ),
                        tint = Color.Transparent
                    )
                }
            }
            EditDescriptionField(value = viewState.descriptionTextValue,
                onChange = { onEditDescription(it) }
            )
            PileDetailSettings(
                viewState = viewState,
                onSetPileMode = onSetPileMode,
                onSetPileLimit = onSetPileLimit
            )
            Text(
                text = "Statistics",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 16.dp),
                textAlign = TextAlign.Start
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            BarChart(
                barChartData = BarChartData(
                    bars = viewState.completedTaskCounts.mapIndexed { index, value ->
                        BarChartData.Bar(
                            label = (viewState.completedTaskCounts.size - index).toString(),
                            value = value,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                ),
                // Optional properties.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .weight(1f, false),
                animation = simpleChartAnimation(),
                barDrawer = SimpleBarDrawer(),
                xAxisDrawer = SimpleXAxisDrawer(),
                yAxisDrawer = SimpleYAxisDrawer(),
                labelDrawer = SimpleValueDrawer()
            )
        }
        Button(
            onClick = onDeletePile,
            enabled = viewState.canDeleteOrEdit,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(text = "Delete")
        }
    }
}

@PreviewMainScreen
@Composable
fun PileDetailScreenPreview() {
    PileyTheme {
        Surface {
            val viewState = PileDetailViewState(
                Pile(
                    name = "Some Pile",
                    description = "Some description",
                    pileMode = PileMode.FIFO,
                    pileLimit = 20
                ),
                titleTextValue = "some text",
                descriptionTextValue = "some description"
            )
            PileDetailScreen(viewState = viewState)
        }
    }
}