import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class WeeklyPlanViewModel : ViewModel() {

    // ✅ 선택된 요일 상태
    var selectedDay = mutableStateOf("월")

    // ✅ 체크 상태 저장 (요일별 인덱스 → 체크 여부)
    val checkedStatesMap = mutableStateMapOf<String, MutableMap<Int, Boolean>>()

    // ✅ 요일별 실천 계획
    val samplePlans: Map<String, List<String>> = mapOf(
        "월" to listOf("가볍게 스트레칭하기", "물 1.5L 이상 마시기", "계단 이용하기", "명상 5분 하기"),
        "화" to listOf("산책 10~30분 하기", "간단한 스트레칭 5~10분", "간단한 필라테스 동작 따라 하기", "가벼운 요가 동작 하기"),
        "토" to listOf("밖에서 햇빛 쬐기", "오래된 연락처 정리하기", "노래 크게 따라 부르기", "몸 흔들며 기분전환하기"),
        "일" to listOf("수학 문제를 하루에 한 문제씩 풀어보기", "친구에게 작은 메시지를 보내거나 기다려보기", "오늘 하루 자신에게 칭찬할 점 3가지 적어보가", "좋아하는 음악을 들으며 잠시 휴식하기")
    )

    fun updateSelectedDay(day: String) {
        selectedDay.value = day
    }

    fun getStatesForDay(day: String): MutableMap<Int, Boolean> {
        val existing = checkedStatesMap[day]
        return if (existing != null) {
            existing
        } else {
            val newMap = mutableStateMapOf<Int, Boolean>()  // ✅ 이거!
            checkedStatesMap[day] = newMap
            newMap
        }
    }

    fun updateCheck(day: String, index: Int, value: Boolean) {
        val map = getStatesForDay(day)
        map[index] = value // ✅ Compose가 반응하게 됨 (toMutableStateMap 덕분에)
    }
}
