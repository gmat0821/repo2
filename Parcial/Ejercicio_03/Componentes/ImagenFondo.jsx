import {View, ImageBackground, StyleSheet, Dimensions, Image, Text} from 'react-native';
export default function ImagenFondo() {
    return (
        <View style={styles.fondo}>
      <ImageBackground
        style={styles.fondo}
        source={require('../assets/fondo.jpg')}
      >

        <View style={styles.container}>
        <Image
          style={styles.foto}
          source={{ uri: 'https://i.pinimg.com/474x/4e/3b/36/4e3b36b20a6bb6b923a5af7d7feaba46.jpg' }}
        />
        </View>
        <View style={styles.texto}>
        <Text style={styles.texto}>kamisama hajimemashita</Text>
        </View>
      </ImageBackground>
    </View>
  );
}


const styles = StyleSheet.create({
    fondo: {
       flex: 1,
       alignItems: 'center',
       justifyContent: 'center',
       width: Dimensions.get('window').width,
       height: Dimensions.get('window').height,
    },

    foto:{
      opacity: 0.8,
        margin: 20,
        alignContent: 'center',
        width: 200,    
        height: 200,
        borderRadius: 16,
        borderWidth: 10,
        borderColor: 'orange',
        shadowColor: 'red',
        shadowOffset: { width: 0, height: 2 },
        shadowRadius: 4,
        elevation: 5,



    },

    container: {
        flex: 1,    
        justifyContent: 'center',
        alignItems: 'center',
        
    },
    texto: {
        fontFamily: 'times-new-roman',
        fontWeight: 'bold',
        fontSize: 32,
        color: 'red',
        textAlign: 'center',
        backgroundColor: 'rgb(255, 219, 0, 0.5)',
        position: 'absolute',
        top:Dimensions.get('window').height * 0.05,
        left:Dimensions.get('window').width * 0.0,
        right: Dimensions.get('window').width * 0.0,

    }

});
