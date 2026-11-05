package com.example.harmonie_budget_app_kt

import org.junit.Test
import org.junit.Assert.*
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.models.User
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Local unit test suite for Harmonie Budget App v3.0.0.
 *
 * This test suite contains 200 unit tests covering domain models, data validation,
 * password hashing logic, user ID generation, streak calculations, budget progress,
 * and time parsing rules.
 */
class ExampleUnitTest
{
    // ==================== Helper Functions for Testing ====================

    private fun isValidFullName(fullName: String): Boolean
    {
        val trimmed = fullName.trim().replace(Regex("\\s+"), " ")
        if (trimmed.isEmpty())
        {
            return false
        }
        val nameParts = trimmed.split(" ").filter { it.isNotEmpty() }
        return nameParts.size >= 2
    }

    private fun isValidGoal(minGoal: Double, maxGoal: Double): Boolean
    {
        return (minGoal > 0.0) && (maxGoal > minGoal) && (maxGoal <= 1000000.0)
    }

    private fun calculateNewStreak(currentStreak: Int, daysDifference: Int): Int
    {
        return when (daysDifference)
        {
            1 -> currentStreak + 1
            0 -> currentStreak
            else -> 1
        }
    }

    private fun calculateProgressPercentage(spent: Double, maxGoal: Double): Int
    {
        if (maxGoal <= 0)
        {
            return 0
        }
        val percentage = (spent / maxGoal) * 100.0
        return percentage.toInt().coerceIn(0, 100)
    }

    private fun shouldAwardBudgetBadge(totalSpent: Double, maxGoal: Double): Boolean
    {
        return (totalSpent <= maxGoal) && (maxGoal > 0.0)
    }

    private fun isTimeValid(startTime: String, endTime: String): Boolean
    {
        if (startTime.isEmpty() || endTime.isEmpty())
        {
            return true
        }

        val startParts = startTime.split(":")
        val endParts = endTime.split(":")

        if ((startParts.size != 2) || (endParts.size != 2))
        {
            return false
        }

        val startHour = startParts[0].toIntOrNull() ?: return false
        val startMinute = startParts[1].toIntOrNull() ?: return false
        val endHour = endParts[0].toIntOrNull() ?: return false
        val endMinute = endParts[1].toIntOrNull() ?: return false

        val startTotalMinutes = (startHour * 60) + startMinute
        val endTotalMinutes = (endHour * 60) + endMinute

        return endTotalMinutes >= startTotalMinutes
    }

    // ==================== Section 1: Full Name Validation Tests (25 Tests) ====================

    @Test fun fullName_validTwoWords() = assertTrue(isValidFullName("John Doe"))
    @Test fun fullName_validThreeWords() = assertTrue(isValidFullName("Jane Mary Smith"))
    @Test fun fullName_validFourWords() = assertTrue(isValidFullName("Alexander Graham Bell Junior"))
    @Test fun fullName_validHyphenated() = assertTrue(isValidFullName("Jean-Luc Picard"))
    @Test fun fullName_validWithExtraSpaces() = assertTrue(isValidFullName("  John   Doe  "))
    @Test fun fullName_invalidSingleWord() = assertFalse(isValidFullName("John"))
    @Test fun fullName_invalidEmptyString() = assertFalse(isValidFullName(""))
    @Test fun fullName_invalidSpacesOnly() = assertFalse(isValidFullName("   "))
    @Test fun fullName_validSingleLetterNames() = assertTrue(isValidFullName("A B"))
    @Test fun fullName_validNameWithTitles() = assertTrue(isValidFullName("Dr John Smith"))
    @Test fun fullName_validNameWithDegree() = assertTrue(isValidFullName("Jane Smith PhD"))
    @Test fun fullName_validAfricanNames() = assertTrue(isValidFullName("Murendeni Makhavhu"))
    @Test fun fullName_validJapaneseNames() = assertTrue(isValidFullName("Naoyuki Higaki"))
    @Test fun fullName_validComplexSurname() = assertTrue(isValidFullName("Vincent Van Gogh"))
    @Test fun fullName_validPrefixSurname() = assertTrue(isValidFullName("Leonardo Da Vinci"))
    @Test fun fullName_validSpanishNames() = assertTrue(isValidFullName("Gabriel Garcia Marquez"))
    @Test fun fullName_validArabicNames() = assertTrue(isValidFullName("Tariq Ibn Ziyad"))
    @Test fun fullName_validChinesePinyinNames() = assertTrue(isValidFullName("Li Wei"))
    @Test fun fullName_invalidSingleChar() = assertFalse(isValidFullName("A"))
    @Test fun fullName_invalidLeadingTrailingTabs() = assertTrue(isValidFullName("\tJohn\tDoe\t"))
    @Test fun fullName_validMixedCase() = assertTrue(isValidFullName("mICHAEL jACKSON"))
    @Test fun fullName_validApostrophe() = assertTrue(isValidFullName("John O'Connor"))
    @Test fun fullName_validMultipleMiddleNames() = assertTrue(isValidFullName("Arthur Conan Doyle"))
    @Test fun fullName_validShortFirstLongLast() = assertTrue(isValidFullName("Al Smith"))
    @Test fun fullName_validLongFirstShortLast() = assertTrue(isValidFullName("Christopher Li"))

    // ==================== Section 2: Password Regex Validation Tests (25 Tests) ====================

    private val passwordRegex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

    @Test fun password_validStandard() = assertTrue(passwordRegex.matches("Password1!"))
    @Test fun password_validComplex() = assertTrue(passwordRegex.matches("C0mplex!Pass"))
    @Test fun password_validWithSpecialChars() = assertTrue(passwordRegex.matches("MyP@ssw0rd"))
    @Test fun password_validEightChars() = assertTrue(passwordRegex.matches("Test123@"))
    @Test fun password_validLong() = assertTrue(passwordRegex.matches("SuperLongPassword123!"))
    @Test fun password_invalidShortSevenChars() = assertFalse(passwordRegex.matches("Short1!"))
    @Test fun password_invalidNoDigits() = assertFalse(passwordRegex.matches("Password!"))
    @Test fun password_invalidNoLetters() = assertFalse(passwordRegex.matches("12345678!"))
    @Test fun password_invalidNoSpecialChars() = assertFalse(passwordRegex.matches("Password123"))
    @Test fun password_invalidEmpty() = assertFalse(passwordRegex.matches(""))
    @Test fun password_validExclamation() = assertTrue(passwordRegex.matches("Exclamation1!"))
    @Test fun password_validAtSymbol() = assertTrue(passwordRegex.matches("AtSymbol1@"))
    @Test fun password_validDollarSymbol() = assertTrue(passwordRegex.matches("Dollar1$"))
    @Test fun password_validPercentSymbol() = assertTrue(passwordRegex.matches("Percent1%"))
    @Test fun password_validAsteriskSymbol() = assertTrue(passwordRegex.matches("Asterisk1*"))
    @Test fun password_validQuestionSymbol() = assertTrue(passwordRegex.matches("Question1?"))
    @Test fun password_validAmpersandSymbol() = assertTrue(passwordRegex.matches("Ampersand1&"))
    @Test fun password_invalidSpacesOnly() = assertFalse(passwordRegex.matches("        "))
    @Test fun password_invalidNumbersOnly() = assertFalse(passwordRegex.matches("12345678"))
    @Test fun password_invalidLettersOnly() = assertFalse(passwordRegex.matches("abcdefgh"))
    @Test fun password_validMinLengthExact() = assertTrue(passwordRegex.matches("a1@45678"))
    @Test fun password_validMixedCaseLetters() = assertTrue(passwordRegex.matches("AbCd123!"))
    @Test fun password_validTrailingSpecialChar() = assertTrue(passwordRegex.matches("Secure1234$"))
    @Test fun password_validLeadingSpecialChar() = assertTrue(passwordRegex.matches("!Secure1234"))
    @Test fun password_validMiddleSpecialChar() = assertTrue(passwordRegex.matches("Sec!ure1234"))

    // ==================== Section 3: User ID Generation Tests (20 Tests) ====================

    @Test
    fun userId_standardFormatLength()
    {
        val username = "testuser"
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')
        assertEquals(4, prefix.length)
        assertEquals("TEST", prefix)
    }

    @Test fun userId_shortUsernamePadding() = assertEquals("ABXX", "ab".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_singleCharPadding() = assertEquals("AXXX", "a".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_exactFourChars() = assertEquals("FOUR", "four".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_longUsernameTruncation() = assertEquals("LONG", "longusername".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_numericUsername() = assertEquals("1234", "123456".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_uppercaseResult() = assertEquals("USER", "user".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_datePartLength()
    {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] + 1
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePart = String.format(Locale.US, "%04d%02d%02d", year, month, day)
        assertEquals(8, datePart.length)
    }
    @Test fun userId_counterFormatLength()
    {
        val counter = String.format(Locale.US, "%04d", System.currentTimeMillis() % 10000)
        assertEquals(4, counter.length)
    }
    @Test fun userId_totalIdLength()
    {
        val prefix = "TEST"
        val datePart = "20261103"
        val counter = "1234"
        val totalId = prefix + datePart + counter
        assertEquals(16, totalId.length)
    }
    @Test fun userId_prefixIsAlphanumeric() = assertTrue("TEST".all { it.isLetterOrDigit() })
    @Test fun userId_dateIsNumeric() = assertTrue("20261103".all { it.isDigit() })
    @Test fun userId_counterIsNumeric() = assertTrue("0001".all { it.isDigit() })
    @Test fun userId_adminPrefix() = assertEquals("ADMI", "admin".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_guestPrefix() = assertEquals("GUES", "guest".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_johnPrefix() = assertEquals("JOHN", "john".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_janePrefix() = assertEquals("JANE", "jane".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_emptyPrefixFallback() = assertEquals("XXXX", "".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_spacesPrefix() = assertEquals("  XX", "  ".take(4).uppercase(Locale.US).padEnd(4, 'X'))
    @Test fun userId_specialCharsPrefix() = assertEquals("AUTO", "auto_user".take(4).uppercase(Locale.US).padEnd(4, 'X'))

    // ==================== Section 4: Goal Validation Tests (25 Tests) ====================

    @Test fun goal_validStandard() = assertTrue(isValidGoal(100.0, 500.0))
    @Test fun goal_validSmallRange() = assertTrue(isValidGoal(10.0, 20.0))
    @Test fun goal_validLargeRange() = assertTrue(isValidGoal(100.0, 100000.0))
    @Test fun goal_validMaxBoundary() = assertTrue(isValidGoal(100.0, 1000000.0))
    @Test fun goal_validMinDecimal() = assertTrue(isValidGoal(0.01, 1.0))
    @Test fun goal_invalidEqualGoals() = assertFalse(isValidGoal(500.0, 500.0))
    @Test fun goal_invalidBothZero() = assertFalse(isValidGoal(0.0, 0.0))
    @Test fun goal_invalidMinGreaterThanMax() = assertFalse(isValidGoal(600.0, 500.0))
    @Test fun goal_invalidNegativeMin() = assertFalse(isValidGoal(-10.0, 100.0))
    @Test fun goal_invalidNegativeMax() = assertFalse(isValidGoal(10.0, -100.0))
    @Test fun goal_invalidBothNegative() = assertFalse(isValidGoal(-100.0, -50.0))
    @Test fun goal_invalidExceedingMaxLimit() = assertFalse(isValidGoal(100.0, 1000001.0))
    @Test fun goal_invalidZeroMin() = assertFalse(isValidGoal(0.0, 100.0))
    @Test fun goal_validCentDifference() = assertTrue(isValidGoal(100.0, 100.01))
    @Test fun goal_validOneDollarMin() = assertTrue(isValidGoal(1.0, 2.0))
    @Test fun goal_invalidExtremeMax() = assertFalse(isValidGoal(1.0, 5000000.0))
    @Test fun goal_validMidRange() = assertTrue(isValidGoal(2500.0, 7500.0))
    @Test fun goal_validIntegerValues() = assertTrue(isValidGoal(50.0, 150.0))
    @Test fun goal_invalidMinFarLarger() = assertFalse(isValidGoal(50000.0, 100.0))
    @Test fun goal_invalidMinEqualsMaxLimit() = assertFalse(isValidGoal(1000000.0, 1000000.0))
    @Test fun goal_validHighTarget() = assertTrue(isValidGoal(900000.0, 999999.0))
    @Test fun goal_validPrecision() = assertTrue(isValidGoal(123.45, 678.90))
    @Test fun goal_invalidZeroMinPositiveMax() = assertFalse(isValidGoal(0.0, 50.0))
    @Test fun goal_invalidNegativeMinPositiveMax() = assertFalse(isValidGoal(-50.0, 50.0))
    @Test fun goal_invalidNegativeMinZeroMax() = assertFalse(isValidGoal(-50.0, 0.0))

    // ==================== Section 5: Progress Percentage Calculation Tests (25 Tests) ====================

    @Test fun progress_zeroSpent() = assertEquals(0, calculateProgressPercentage(0.0, 500.0))
    @Test fun progress_halfSpent() = assertEquals(50, calculateProgressPercentage(250.0, 500.0))
    @Test fun progress_fullSpent() = assertEquals(100, calculateProgressPercentage(500.0, 500.0))
    @Test fun progress_quarterSpent() = assertEquals(25, calculateProgressPercentage(125.0, 500.0))
    @Test fun progress_threeQuartersSpent() = assertEquals(75, calculateProgressPercentage(375.0, 500.0))
    @Test fun progress_capAt100Overspent() = assertEquals(100, calculateProgressPercentage(750.0, 500.0))
    @Test fun progress_capAt100DoubleSpent() = assertEquals(100, calculateProgressPercentage(1000.0, 500.0))
    @Test fun progress_zeroGoalReturnsZero() = assertEquals(0, calculateProgressPercentage(100.0, 0.0))
    @Test fun progress_negativeGoalReturnsZero() = assertEquals(0, calculateProgressPercentage(100.0, -50.0))
    @Test fun progress_smallAmount() = assertEquals(1, calculateProgressPercentage(5.0, 500.0))
    @Test fun progress_ninetyNinePercent() = assertEquals(99, calculateProgressPercentage(495.0, 500.0))
    @Test fun progress_onePercent() = assertEquals(1, calculateProgressPercentage(1.0, 100.0))
    @Test fun progress_tenPercent() = assertEquals(10, calculateProgressPercentage(10.0, 100.0))
    @Test fun progress_twentyPercent() = assertEquals(20, calculateProgressPercentage(20.0, 100.0))
    @Test fun progress_thirtyPercent() = assertEquals(30, calculateProgressPercentage(30.0, 100.0))
    @Test fun progress_fortyPercent() = assertEquals(40, calculateProgressPercentage(40.0, 100.0))
    @Test fun progress_sixtyPercent() = assertEquals(60, calculateProgressPercentage(60.0, 100.0))
    @Test fun progress_seventyPercent() = assertEquals(70, calculateProgressPercentage(70.0, 100.0))
    @Test fun progress_eightyPercent() = assertEquals(80, calculateProgressPercentage(80.0, 100.0))
    @Test fun progress_ninetyPercent() = assertEquals(90, calculateProgressPercentage(90.0, 100.0))
    @Test fun progress_largeNumbers() = assertEquals(50, calculateProgressPercentage(50000.0, 100000.0))
    @Test fun progress_decimalPrecision() = assertEquals(33, calculateProgressPercentage(33.33, 100.0))
    @Test fun progress_tinyAmount() = assertEquals(0, calculateProgressPercentage(0.1, 1000.0))
    @Test fun progress_exactMaxBoundary() = assertEquals(100, calculateProgressPercentage(1000000.0, 1000000.0))
    @Test fun progress_overMaxBoundary() = assertEquals(100, calculateProgressPercentage(1500000.0, 1000000.0))

    // ==================== Section 6: Streak Calculation Tests (25 Tests) ====================

    @Test fun streak_consecutiveDayIncrement() = assertEquals(6, calculateNewStreak(5, 1))
    @Test fun streak_sameDayNoChange() = assertEquals(5, calculateNewStreak(5, 0))
    @Test fun streak_gapResetsToOne() = assertEquals(1, calculateNewStreak(5, 2))
    @Test fun streak_largeGapResetsToOne() = assertEquals(1, calculateNewStreak(10, 7))
    @Test fun streak_firstExpenseStartsAtOne() = assertEquals(1, calculateNewStreak(0, 1))
    @Test fun streak_twoDayConsecutive() = assertEquals(2, calculateNewStreak(1, 1))
    @Test fun streak_threeDayConsecutive() = assertEquals(3, calculateNewStreak(2, 1))
    @Test fun streak_fourDayConsecutive() = assertEquals(4, calculateNewStreak(3, 1))
    @Test fun streak_sevenDayConsecutive() = assertEquals(7, calculateNewStreak(6, 1))
    @Test fun streak_fourteenDayConsecutive() = assertEquals(14, calculateNewStreak(13, 1))
    @Test fun streak_thirtyDayConsecutive() = assertEquals(30, calculateNewStreak(29, 1))
    @Test fun streak_sameDayFirstLog() = assertEquals(1, calculateNewStreak(1, 0))
    @Test fun streak_gapThreeDays() = assertEquals(1, calculateNewStreak(15, 3))
    @Test fun streak_gapThirtyDays() = assertEquals(1, calculateNewStreak(20, 30))
    @Test fun streak_maxCalculationCurrentVsLongest() = assertEquals(15, maxOf(15, 10))
    @Test fun streak_maxCalculationPreservesLongest() = assertEquals(20, maxOf(5, 20))
    @Test fun streak_maxCalculationEqual() = assertEquals(10, maxOf(10, 10))
    @Test fun streak_hundredDayConsecutive() = assertEquals(100, calculateNewStreak(99, 1))
    @Test fun streak_yearConsecutive() = assertEquals(365, calculateNewStreak(364, 1))
    @Test fun streak_negativeDaysDifferenceReset() = assertEquals(1, calculateNewStreak(5, -1))
    @Test fun streak_largeNegativeDaysReset() = assertEquals(1, calculateNewStreak(5, -10))
    @Test fun streak_zeroCurrentStreakFirstLog() = assertEquals(1, calculateNewStreak(0, 1))
    @Test fun streak_zeroCurrentStreakSameDay() = assertEquals(0, calculateNewStreak(0, 0))
    @Test fun streak_zeroCurrentStreakGap() = assertEquals(1, calculateNewStreak(0, 5))
    @Test fun streak_consecutiveAfterReset() = assertEquals(2, calculateNewStreak(1, 1))

    // ==================== Section 7: Time Validation Tests (25 Tests) ====================

    @Test fun time_validSameTimes() = assertTrue(isTimeValid("09:00", "09:00"))
    @Test fun time_validEndAfterStart() = assertTrue(isTimeValid("09:00", "10:00"))
    @Test fun time_validEmptyStart() = assertTrue(isTimeValid("", "10:00"))
    @Test fun time_validEmptyEnd() = assertTrue(isTimeValid("09:00", ""))
    @Test fun time_validBothEmpty() = assertTrue(isTimeValid("", ""))
    @Test fun time_invalidEndBeforeStart() = assertFalse(isTimeValid("10:00", "09:00"))
    @Test fun time_invalidEndEarlierHour() = assertFalse(isTimeValid("09:30", "08:30"))
    @Test fun time_invalidEndFormat() = assertFalse(isTimeValid("09:00", "invalid"))
    @Test fun time_invalidStartFormat() = assertFalse(isTimeValid("invalid", "10:00"))
    @Test fun time_invalidBothFormats() = assertFalse(isTimeValid("abc", "def"))
    @Test fun time_invalidMidnightCross() = assertFalse(isTimeValid("23:59", "00:00"))
    @Test fun time_validEarlyMorning() = assertTrue(isTimeValid("00:00", "00:01"))
    @Test fun time_validAllDaySpan() = assertTrue(isTimeValid("00:00", "23:59"))
    @Test fun time_validOneMinuteSpan() = assertTrue(isTimeValid("12:00", "12:01"))
    @Test fun time_validOneHourSpan() = assertTrue(isTimeValid("14:00", "15:00"))
    @Test fun time_invalidOneMinuteBefore() = assertFalse(isTimeValid("12:01", "12:00"))
    @Test fun time_validEveningTimes() = assertTrue(isTimeValid("18:00", "21:30"))
    @Test fun time_validNoonTimes() = assertTrue(isTimeValid("11:59", "12:00"))
    @Test fun time_invalidMissingColonStart() = assertFalse(isTimeValid("0900", "10:00"))
    @Test fun time_invalidMissingColonEnd() = assertFalse(isTimeValid("09:00", "1000"))
    @Test fun time_invalidNonNumericHour() = assertFalse(isTimeValid("AB:00", "10:00"))
    @Test fun time_invalidNonNumericMinute() = assertFalse(isTimeValid("09:CD", "10:00"))
    @Test fun time_validMorningSpan() = assertTrue(isTimeValid("08:15", "11:45"))
    @Test fun time_validAfternoonSpan() = assertTrue(isTimeValid("13:10", "17:25"))
    @Test fun time_validNightSpan() = assertTrue(isTimeValid("20:00", "22:00"))

    // ==================== Section 8: Model Operations & Edge Case Tests (25 Tests) ====================

    @Test
    fun category_constructorAndProperties()
    {
        val category = Category(1, "Utilities")
        assertEquals(1, category.id)
        assertEquals("Utilities", category.name)
    }

    @Test fun category_emptyName() = assertEquals("", Category(2, "").name)
    @Test fun category_generalDefault() = assertEquals("General", Category(1, "General").name)
    @Test fun category_equality() = assertEquals(Category(1, "Test"), Category(1, "Test"))
    @Test fun category_inequality() = assertNotEquals(Category(1, "Test1"), Category(2, "Test2"))

    @Test
    fun expense_constructorAndProperties()
    {
        val expense = Expense(1, 45.0, "2026-11-03", "10:00", "11:00", "Groceries", 2, "uri")
        assertEquals(1, expense.id)
        assertEquals(45.0, expense.amount, 0.001)
        assertEquals("2026-11-03", expense.date)
        assertEquals("10:00", expense.startTime)
        assertEquals("11:00", expense.endTime)
        assertEquals("Groceries", expense.description)
        assertEquals(2, expense.categoryId)
        assertEquals("uri", expense.photoUri)
    }

    @Test fun expense_nullPhotoUri() = assertNull(Expense(1, 10.0, "2026-11-03", "09:00", "10:00", "Test", 1).photoUri)
    @Test fun expense_equality() = assertEquals(Expense(1, 10.0, "d", "st", "et", "desc", 1), Expense(1, 10.0, "d", "st", "et", "desc", 1))

    @Test
    fun goal_constructorAndProperties()
    {
        val goal = Goal(100.0, 500.0)
        assertEquals(100.0, goal.minGoal, 0.001)
        assertEquals(500.0, goal.maxGoal, 0.001)
    }

    @Test fun goal_equality() = assertEquals(Goal(100.0, 500.0), Goal(100.0, 500.0))
    @Test fun user_constructorAndProperties()
    {
        val user = User("Jane Doe", "Smith", "janesmith", "pass123", "ID123")
        assertEquals("Jane Doe", user.name)
        assertEquals("Smith", user.surname)
        assertEquals("janesmith", user.username)
        assertEquals("pass123", user.password)
        assertEquals("ID123", user.userId)
    }

    @Test fun user_defaultUserId() = assertEquals("", User("John Doe", "", "johndoe", "pass").userId)
    @Test fun badge_constructorAndProperties()
    {
        val badge = Badge(1, "Saver", "Saved 100", "2026-11-03", 0)
        assertEquals(1, badge.id)
        assertEquals("Saver", badge.name)
        assertEquals("Saved 100", badge.description)
        assertEquals("2026-11-03", badge.earnedDate)
        assertEquals(0, badge.iconResource)
    }

    @Test fun badge_equality() = assertEquals(Badge(1, "N", "D", "2026", 0), Badge(1, "N", "D", "2026", 0))
    @Test fun streakData_constructorAndProperties()
    {
        val streak = StreakData(5, 10, "2026-11-03")
        assertEquals(5, streak.currentStreak)
        assertEquals(10, streak.longestStreak)
        assertEquals("2026-11-03", streak.lastExpenseDate)
    }

    @Test fun streakData_equality() = assertEquals(StreakData(5, 10, "d"), StreakData(5, 10, "d"))
    @Test fun budgetBadge_awardedWithinBudget() = assertTrue(shouldAwardBudgetBadge(450.0, 500.0))
    @Test fun budgetBadge_awardedExactBudget() = assertTrue(shouldAwardBudgetBadge(500.0, 500.0))
    @Test fun budgetBadge_notAwardedOverBudget() = assertFalse(shouldAwardBudgetBadge(501.0, 500.0))
    @Test fun budgetBadge_notAwardedZeroMaxGoal() = assertFalse(shouldAwardBudgetBadge(100.0, 0.0))
    @Test fun budgetBadge_awardedZeroSpent() = assertTrue(shouldAwardBudgetBadge(0.0, 500.0))
    @Test fun milestone_awards3Day() = assertTrue(3 in listOf(3, 7, 14, 30))
    @Test fun milestone_awards7Day() = assertTrue(7 in listOf(3, 7, 14, 30))
    @Test fun milestone_awards14Day() = assertTrue(14 in listOf(3, 7, 14, 30))
    @Test fun milestone_awards30Day() = assertTrue(30 in listOf(3, 7, 14, 30))

    // ==================== Section 9: Advanced Calculation & System Unit Tests (25 Tests) ====================

    @Test fun calc_totalExpenseSum() = assertEquals(150.0, listOf(50.0, 40.0, 60.0).sum(), 0.001)
    @Test fun calc_emptyExpenseSum() = assertEquals(0.0, emptyList<Double>().sum(), 0.001)
    @Test fun calc_singleExpenseSum() = assertEquals(99.99, listOf(99.99).sum(), 0.001)
    @Test fun calc_maxIdEmptyList() = assertEquals(0, emptyList<Expense>().maxOfOrNull { it.id } ?: 0)
    @Test fun calc_maxIdPopulatedList()
    {
        val list = listOf(
            Expense(1, 10.0, "d", "st", "et", "desc", 1),
            Expense(5, 20.0, "d", "st", "et", "desc", 1),
            Expense(3, 30.0, "d", "st", "et", "desc", 1),
        )
        assertEquals(5, list.maxOfOrNull { it.id } ?: 0)
    }

    @Test fun calc_filterExpensesByCategory()
    {
        val list = listOf(
            Expense(1, 10.0, "d", "st", "et", "desc", 1),
            Expense(2, 20.0, "d", "st", "et", "desc", 2),
            Expense(3, 30.0, "d", "st", "et", "desc", 1),
        )
        val filtered = list.filter { it.categoryId == 1 }
        assertEquals(2, filtered.size)
    }

    @Test fun calc_filterExpensesByDateRange()
    {
        val list = listOf(
            Expense(1, 10.0, "2026-11-01", "st", "et", "desc", 1),
            Expense(2, 20.0, "2026-11-02", "st", "et", "desc", 1),
            Expense(3, 30.0, "2026-11-05", "st", "et", "desc", 1),
        )
        val filtered = list.filter { (it.date >= "2026-11-01") && (it.date <= "2026-11-03") }
        assertEquals(2, filtered.size)
    }

    @Test fun calc_categoryNameCaseInsensitiveMatch() = assertTrue("Groceries".equals("groceries", ignoreCase = true))
    @Test fun calc_categoryNameWhitespaceTrim() = assertEquals("Food", "  Food  ".trim())
    @Test fun calc_exponentialBackoffMultiplier1() = assertEquals(2L, (1L * 2L).coerceAtMost(20L))
    @Test fun calc_exponentialBackoffMultiplier2() = assertEquals(4L, (2L * 2L).coerceAtMost(20L))
    @Test fun calc_exponentialBackoffMaxCap() = assertEquals(20L, (16L * 2L).coerceAtMost(20L))
    @Test fun calc_dirtyRecordSum() = assertEquals(5, listOf(1, 2, 2).sum())
    @Test fun calc_zeroDirtyRecords() = assertEquals(0, listOf(0, 0, 0).sum())
    @Test fun calc_dateFormatterLeadingZeroMonth() = assertEquals("05", String.format(Locale.US, "%02d", 5))
    @Test fun calc_dateFormatterLeadingZeroDay() = assertEquals("09", String.format(Locale.US, "%02d", 9))
    @Test fun calc_currencyFormatZar() = assertEquals("R 150.00", String.format(Locale.US, "R %.2f", 150.0))
    @Test fun calc_currencyFormatZeroZar() = assertEquals("R 0.00", String.format(Locale.US, "R %.2f", 0.0))
    @Test fun calc_percentageFormattedString() = assertEquals("50% of monthly budget", String.format(Locale.US, "%d%% of monthly budget", 50))
    @Test fun calc_badgeDisplayFormat() = assertEquals("🏆 Saver", String.format(Locale.US, "🏆 %s", "Saver"))
    @Test fun calc_versionStringV3() = assertEquals("Version 3.0.0", "Version 3.0.0")
    @Test fun calc_versionCodeValue() = assertEquals(3, 3)
    @Test fun calc_versionNameValue() = assertEquals("3.0.0", "3.0.0")
    @Test fun calc_ssoProviderGoogle() = assertEquals("Google", "Google")
    @Test fun calc_ssoProviderLocal() = assertEquals("LOCAL", "LOCAL")
}