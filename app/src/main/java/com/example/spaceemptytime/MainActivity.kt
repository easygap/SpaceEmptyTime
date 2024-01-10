package com.example.spaceemptytime

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.spaceemptytime.ui.theme.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timeTable")
val monKey = stringPreferencesKey(WeekValue.MON.name)
val tueKey = stringPreferencesKey(WeekValue.TUE.name)
val wedKey = stringPreferencesKey(WeekValue.WED.name)
val thuKey = stringPreferencesKey(WeekValue.THU.name)
val friKey = stringPreferencesKey(WeekValue.FRI.name)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStore = this.dataStore.data
        val editDataStore = this.dataStore

        val monSchedules = dataStore.map { it[monKey].toString().split("/") }
        val tueSchedules = dataStore.map { it[tueKey].toString().split("/") }
        val wedSchedules = dataStore.map { it[wedKey].toString().split("/") }
        val thuSchedules = dataStore.map { it[thuKey].toString().split("/") }
        val friSchedules = dataStore.map { it[friKey].toString().split("/") }

        setContent {
            SpaceEmptyTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val monState = monSchedules.collectAsState(initial = emptyList())
                    val tueState = tueSchedules.collectAsState(initial = emptyList())
                    val wedState = wedSchedules.collectAsState(initial = emptyList())
                    val thuState = thuSchedules.collectAsState(initial = emptyList())
                    val friState = friSchedules.collectAsState(initial = emptyList())
                    val schedulesMap =
                        mapOf(
                            WeekValue.MON to monState.value,
                            WeekValue.TUE to tueState.value,
                            WeekValue.WED to wedState.value,
                            WeekValue.THU to thuState.value,
                            WeekValue.FRI to friState.value
                        )


                    var showAddScheduleDialog by remember {
                        mutableStateOf(false)
                    }
                    var isShowEmptyTime by remember {
                        mutableStateOf(false)
                    }
                    val coroutineScope = rememberCoroutineScope()
                    TimeTable(
                        isShowEmptyTime,
                        schedulesMap,
                        showScheduleDialog = { showAddScheduleDialog = true },
                        changeShowTime = { isShowEmptyTime = it },
                        resetSchedules = {
                            coroutineScope.launch {
                                editDataStore.edit { it.clear() }
                            }
                        }
                    )
                    if (showAddScheduleDialog) {
                        Dialog(onDismissRequest = { showAddScheduleDialog = false }) {
                            AddScheduleDialog {
                                showAddScheduleDialog = false
                                val weekValue = it.first
                                val times = it.second
                                val currentSchedule =
                                    schedulesMap[weekValue].orEmpty().toMutableList()
                                currentSchedule.addAll(times)
                                currentSchedule.distinct()
                                var insertString = ""
                                currentSchedule.forEach { time ->
                                    insertString += "$time/"
                                }
                                val key = when (weekValue) {
                                    WeekValue.MON -> monKey
                                    WeekValue.TUE -> tueKey
                                    WeekValue.WED -> wedKey
                                    WeekValue.THU -> thuKey
                                    WeekValue.FRI -> friKey
                                }
                                coroutineScope.launch {
                                    editDataStore.edit { preference ->
                                        preference[key] = insertString
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val cellModifier = Modifier
    .height(30.dp)
    .fillMaxWidth()
val notSelectedModifier = cellModifier.border(border = BorderStroke(0.5.dp, Color.Gray))
fun selectedModifier(color: Color): Modifier = cellModifier.background(color)

enum class WeekValue(val weekName: String) {
    MON("월요일"),
    TUE("화요일"),
    WED("수요일"),
    THU("목요일"),
    FRI("금요일")
}

val timeList = arrayOf(
    "09:30",
    "10:00",
    "10:30",
    "11:00",
    "11:30",
    "12:00",
    "12:30",
    "13:00",
    "13:30",
    "14:00",
    "14:30",
    "15:00",
    "15:30",
    "16:00",
    "16:30",
    "17:00",
    "17:30",
    "18:00",
    "18:30"
)

@Composable
fun TimeTable(
    isShowEmptyTime: Boolean,
    scheduleMap: Map<WeekValue, List<String>>,
    showScheduleDialog: () -> Unit,
    changeShowTime: (Boolean) -> Unit,
    resetSchedules: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clickable { changeShowTime(!isShowEmptyTime) }
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes =
                    if (isShowEmptyTime) R.drawable.ic_baseline_check_circle_24 else R.drawable.ic_baseline_check_circle_outline_24
                Image(painter = painterResource(id = iconRes), contentDescription = null)
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "공강시간보기",
                    color = if (isShowEmptyTime) Blue400 else Color.Unspecified
                )
            }

            Button(
                onClick = showScheduleDialog,
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue400)
            ) {
                Text(text = "수업추가", color = Color.White)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TimeColumn(modifier = Modifier.weight(1f))
            WeekValue.values().forEach {
                ScheduleColumn(
                    weekValue = it,
                    scheduleList = scheduleMap[it].orEmpty(),
                    modifier = Modifier.weight(1f),
                    showEmptyTime = isShowEmptyTime
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "초기화",
            color = Color.Red,
            modifier = Modifier
                .clickable { resetSchedules() }
                .border(BorderStroke(1.dp, color = Color.Red), shape = RoundedCornerShape(3.dp))
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun TimeColumn(modifier: Modifier) {
    Column(modifier = modifier) {
        Spacer(modifier = cellModifier.background(Blue700))
        timeList.forEach { time ->
            Text(
                text = time,
                modifier = cellModifier
                    .background(Blue700),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
fun ScheduleColumn(
    modifier: Modifier,
    weekValue: WeekValue,
    scheduleList: List<String>,
    showEmptyTime: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weekValue.weekName,
            modifier = cellModifier
                .background(Blue400),
            textAlign = TextAlign.Center,
            color = Color.White
        )
        val selectedColor = if (showEmptyTime) Gold else Blue300
        timeList.forEach { time ->
            val isScheduleTime =
                if (showEmptyTime) !scheduleList.contains(time) else scheduleList.contains(time)
            val text = if (isScheduleTime && !showEmptyTime) "1" else ""
            Text(
                text = text,
                modifier = if (isScheduleTime) selectedModifier(selectedColor) else notSelectedModifier,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpaceEmptyTimeTheme {
        TimeTable(false, emptyMap(), {}, {}) {}
    }
}