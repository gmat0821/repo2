import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

export default function TicTacToeScreen() {
  const [board, setBoard] = useState(Array(9).fill(null));
  const [isXNext, setIsXNext] = useState(true);

  const calculateWinner = (squares) => {
    const lines = [
      [0, 1, 2], [3, 4, 5], [6, 7, 8],
      [0, 3, 6], [1, 4, 7], [2, 5, 8],
      [0, 4, 8], [2, 4, 6]
    ];
    for (let i = 0; i < lines.length; i++) {
      const [a, b, c] = lines[i];
      if (squares[a] && squares[a] === squares[b] && squares[a] === squares[c]) {
        return squares[a];
      }
    }
    return null;
  };

  const handlePress = (index) => {
    if (board[index] || calculateWinner(board)) return;
    const newBoard = [...board];
    newBoard[index] = isXNext ? 'X' : 'O';
    setBoard(newBoard);
    setIsXNext(!isXNext);
  };

  const winner = calculateWinner(board);
  const isDraw = !winner && board.every((square) => square !== null);

  const resetGame = () => {
    setBoard(Array(9).fill(null));
    setIsXNext(true);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Gato (Tic Tac Toe)</Text>
      
      <Text style={styles.status}>
        {winner ? `¡Ganador: ${winner}!` : isDraw ? '¡Empate!' : `Turno de: ${isXNext ? 'X' : 'O'}`}
      </Text>

      <View style={styles.board}>
        {board.map((cell, index) => (
          <TouchableOpacity key={index} style={styles.cell} onPress={() => handlePress(index)}>
            <Text style={[styles.cellText, cell === 'X' ? styles.xText : styles.oText]}>{cell}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <TouchableOpacity style={styles.resetButton} onPress={resetGame}>
        <Text style={styles.resetText}>Reiniciar Juego</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#F3F4F6' },
  title: { fontSize: 26, fontWeight: 'bold', color: '#1F2937', marginBottom: 20 },
  status: { fontSize: 20, color: '#4F46E5', marginBottom: 30, fontWeight: '600' },
  board: { width: 300, height: 300, flexDirection: 'row', flexWrap: 'wrap', backgroundColor: '#E5E7EB', borderRadius: 15, padding: 5 },
  cell: { width: '31%', height: '31%', backgroundColor: '#fff', margin: '1%', borderRadius: 10, justifyContent: 'center', alignItems: 'center', elevation: 2 },
  cellText: { fontSize: 40, fontWeight: 'bold' },
  xText: { color: '#EF4444' },
  oText: { color: '#3B82F6' },
  resetButton: { marginTop: 40, backgroundColor: '#1F2937', paddingVertical: 15, paddingHorizontal: 30, borderRadius: 10 },
  resetText: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
});