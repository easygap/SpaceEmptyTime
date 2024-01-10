package com.example.spaceemptytime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.spaceemptytime.ui.theme.Blue400

@Composable
fun AddScheduleDialog(saveTimeTable: (Pair<WeekValue, Array<String>>) -> Unit) {
    var startTime by remember {
        mutableStateOf(0)
    }
    var endTime by remember {
        mutableStateOf(0)
    }
    var weekValue by remember {
        mutableStateOf(WeekValue.MON)
    }
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(5.dp))
            .padding(8.dp)
    ) {
        Text(text = "수업추가", fontSize = 20.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SelectWeekView(
                modifier = Modifier.weight(1f),
                selected = weekValue == WeekValue.MON,
                weekValue = WeekValue.MON,
                onClick = { weekValue = it }
            )
            SelectWeekView(
                modifier = Modifier.weight(1f),
                selected = weekValue == WeekValue.TUE,
                weekValue = WeekValue.TUE,
                onClick = { weekValue = it }
            )
            SelectWeekView(
                modifier = Modifier.weight(1f),
                selected = weekValue == WeekValue.WED,
                weekValue = WeekValue.WED,
                onClick = { weekValue = it }
            )
            SelectWeekView(
                modifier = Modifier.weight(1f),
                selected = weekValue == WeekValue.THU,
                weekValue = WeekValue.THU,
                onClick = { weekValue = it }
            )
            SelectWeekView(
                modifier = Modifier.weight(1f),
                selected = weekValue == WeekValue.FRI,
                weekValue = WeekValue.FRI,
                onClick = { weekValue = it }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberPicker(
                value = startTime,
                onValueChange = { startTime = it },
                label = { timeList[it] },
                range = 0..endTime,
                dividersColor = Blue400
            )
            Text(text = "부터")
            NumberPicker(
                value = endTime,
                onValueChange = { endTime = it },
                label = { timeList[it] },
                range = startTime until timeList.size,
                dividersColor = Blue400
            )
            Text("까지")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                saveTimeTable(
                    Pair(
                        weekValue,
                        timeList.sliceArray(startTime..endTime)
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Blue400)
        ) {
            Text(text = "저장", color = Color.White)
        }
    }
}

@Composable
fun SelectWeekView(
    modifier: Modifier,
    selected: Boolean,
    weekValue: WeekValue,
    onClick: (WeekValue) -> Unit
) {
    val color = if (selected) Blue400 else Color.Gray
    Text(
        text = weekValue.weekName,
        modifier = modifier
            .padding(3.dp)
            .clickable { onClick(weekValue) }
            .border(
                BorderStroke(
                    1.dp,
                    color = color
                ),
                shape = RoundedCornerShape(3.dp)
            ),
        color = color,
        textAlign = TextAlign.Center
    )
}
