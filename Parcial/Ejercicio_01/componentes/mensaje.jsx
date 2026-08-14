import { View,Text,StyleSheet} from "react-native";

//const mitexto="Mensaje desde un objeto";
//const num=22

//const double=n=>{
  //  return n*2;
//}; 

export default function mensaje(props  ) {
    
    return (
        <View>
            <Text style={[styles.color_naranja,styles.backgroundcolor_gris]}>{props.titulo}</Text>
            <Text style={[styles.font_size, styles.color_negro]}> {props.numero}</Text>
        </View>
    );
}

const styles={
    color_naranja: {
        color: 'orange',
    },

    font_size: {
        fontSize: 30,
    },

    backgroundcolor_gris: {
        backgroundColor: 'gray'
    }
}

