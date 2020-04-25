package dev.nhyne.tictactoe.domain

object Board {
    val wins: Unit = {//Set[Set[Field]] = {
        val horizontalWins = Set(
            Set(1, 2, 3),
            Set(4, 5, 6),
            Set(7, 8, 9),
        )
        val verticalWins = Set(
            Set(1, 4, 7),
            Set(2, 5, 8),
            Set(3, 6, 9),
        )

        val diagnalWins = Set(
            Set(1, 5, 9),
            Set(3, 5, 7)
        )

//        (horizontalWins ++ verticalWins ++ diagnalWins).flatMap(Field.withValue)
//        Set(Set(Field(name = "cool", val = 1)))
    }
}

