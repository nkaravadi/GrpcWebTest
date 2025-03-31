import React, { useState, useEffect, useRef } from "react";
import { AllCommunityModule, ModuleRegistry } from "ag-grid-community";
import { AgGridReact } from "ag-grid-react";
import { getStockUpdates } from './StockDataService';

ModuleRegistry.registerModules([AllCommunityModule]);


function App() {
  const [rowData, setRowData] = useState([]);
  const gridRef = useRef(null);

  const columnDefs = [
    { headerName: 'Symbol', field: 'symbol' },
    { headerName: 'Price', field: 'price' },
    { headerName: 'Timestamp', field: 'timestamp' },
  ];

  useEffect(() => {
    const stream = getStockUpdates('AAPL', (update) => {
      setRowData((prevData) => [...prevData, update]);
      if (gridRef.current) {
        gridRef.current.api.ensureIndexVisible(rowData.length - 1); // Scroll to new data
      }
    });

    return () => {
      if (stream && stream.cancel) {
        stream.cancel(); // Cancel the stream when the component unmounts
      }
    };
  }, []);

  return (
      <div className="ag-theme-alpine" style={{ height: '400px', width: '600px' }}>
        <AgGridReact
            ref={gridRef}
            columnDefs={columnDefs}
            rowData={rowData}
        />
      </div>
  );
}

export default App;
