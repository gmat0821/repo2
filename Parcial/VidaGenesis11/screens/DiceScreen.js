import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import Animated, { useSharedValue, useAnimatedStyle, withTiming, Easing } from 'react-native-reanimated';
import { Ionicons } from '@expo/vector-icons';

export default function DiceScreen() {
  const [diceValue, setDiceValue] = useState(1);
  const rotation = useSharedValue(0);

  const rollDice = () => {
    rotation.value = withTiming(rotation.value + 360, {
      duration: 500,
      easing: Easing.bezier(0.25, 0.1, 0.25, 1),
    });
    setTimeout(() => {
      setDiceValue(Math.floor(Math.random() * 6) + 1);
    }, 250);
  };

  const animatedStyle = useAnimatedStyle(() => {
    return {
      transform: [{ rotate: `${rotation.value}deg` }],
    };
  });

  const getIconName = () => {
    const icons = ['dice-outline', 'dice-outline', 'dice-outline', 'dice-outline', 'dice-outline', 'dice-outline'];
    return icons[diceValue - 1];
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Lanzador de Dados</Text>
      <Animated.View style={[styles.diceContainer, animatedStyle]}>
        <Ionicons name="cube" size={120} color="#4F46E5" />
        <Text style={styles.diceNumber}>{diceValue}</Text>
      </Animated.View>
      <TouchableOpacity style={styles.button} onPress={rollDice}>
        <Text style={styles.buttonText}>Lanzar</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#F3F4F6' },
  title: { fontSize: 24, fontWeight: 'bold', color: '#1F2937', marginBottom: 40 },
  diceContainer: { width: 150, height: 150, backgroundColor: '#fff', borderRadius: 30, justifyContent: 'center', alignItems: 'center', elevation: 10, shadowColor: '#4F46E5', shadowOpacity: 0.3, shadowRadius: 15 },
  diceNumber: { position: 'absolute', fontSize: 40, fontWeight: 'bold', color: '#fff' },
  button: { marginTop: 50, backgroundColor: '#4F46E5', paddingVertical: 15, paddingHorizontal: 40, borderRadius: 30, elevation: 5 },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
});