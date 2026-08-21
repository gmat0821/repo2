import { View, Text, TextInput, Button, ScrollView, StyleSheet, Dimensions } from 'react-native';
import { useState } from 'react';


export default function MyInputText() {
    const [texto, setText] = useState('');
    const [enviar, setEnviar] = useState('');


    return (
        <View>
            <ScrollView>

            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
            <Text> {enviar}</Text>
           
            <TextInput
                placeholder="Escribe aqui..."
                onChangeText={t => setText(t)}
            />
            <Button
                title="Enviar"
                onPress={() => setEnviar(texto)}
            />

            </ScrollView>
        </View> 
    );
}

const styles = StyleSheet.create({
  input: {
    width: Dimensions.get('window').width,
  },
});
