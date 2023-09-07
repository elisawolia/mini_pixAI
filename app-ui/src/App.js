import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

function App() {

   const [songs, setSongs] = useState([]);

   useEffect(() => {
      fetch('http://95.163.229.127:8080/api/songs_list')
         .then((res) => res.json())
         .then((data) => {
            console.log(data);
            setSongs(data?.songsList);
         })
         .catch((err) => {
            console.log(err.message);
         });
   }, []);


  return (
    <main>
      <Container>
        <Box>
          <Card>
            <Typography variant="h2">MINI pixAI</Typography>
            {songs?.map((element) => {
                return <Card sx={{height: 100}}>
                    <Typography variant="h5" component="div">
                        {element?.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {element?.description}
                    </Typography>
                </Card>
             })}
          </Card>
        </Box>
      </Container>
    </main>
  );
}

export default App;
