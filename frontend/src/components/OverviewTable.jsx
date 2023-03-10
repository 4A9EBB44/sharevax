import {useEffect, useState} from 'react';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import {fetchActiveDeliveries} from '../services/services';

const tableHeader = [
	{id: 'startHarbor', label: 'Supplier', minWidth: 21},
	{id: 'destinationHarbor', label: 'Destination', minWidth: 10},
	{id: 'vaccineType', label: 'Vaccine', minWidth: 10},
	{id: 'quantity', label: 'Quantity', minWidth: 20},
	{id: 'createdAt', label: 'Order Date', minWidth: 21},
	{id: 'estimatedArrivalDate', label: 'Estimated Arrival Date', minWidth: 21},
	{id: 'deliveryStatus', label: 'Status', minWidth: 21},
	{id: 'urgency', label: 'Urgency', minWidth: 21},
];

export default function OverviewTable({onNextDay, setUpdated}) {
	const [page, setPage] = useState(0);
	const [rowsPerPage, setRowsPerPage] = useState(10);
	const [activeDeliveriesData, setActiveDeliveriesData] = useState([]);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		if (onNextDay) {
			setActiveDeliveriesData([]);
			setLoading(true);
			fetchActiveDeliveriesData();
			setUpdated(true);
		}
	}, [onNextDay]);

	useEffect(() => {
		fetchActiveDeliveriesData();
	}, []);

	const fetchActiveDeliveriesData = async () => {
		const result = await fetchActiveDeliveries();
		setActiveDeliveriesData(result.data);
		setLoading(false);
	};

	const handleChangePage = (event, newPage) => {
		setPage(newPage);
	};

	const handleChangeRowsPerPage = (event) => {
		setRowsPerPage(+event.target.value);
		setPage(0);
	};

	console.log('New json\n', activeDeliveriesData);

	function formatDate(d) {
		let date = new Date(d);
		return date.toLocaleDateString('en-US');
	}

	function formateStatus(s) {
		if (s == 'IN_TIME') {
			return <div className='text-green-500'>In Time</div>;
		} else if (s == 'DELAYED') {
			return <div className='text-orange-500'>Delayed</div>;
		} else if (s == 'DELIVERED') {
			return <div className='text-gray-500'>Delivered</div>;
		}
	}
	function formateUrgency(u) {
		if (u == 'NORMAL') {
			return <div className='text-green-500'>???? Normal</div>;
		} else if (u == 'URGENT') {
			return <div className='text-orange-500'>???? Urgent</div>;
		} else if (u == 'CRITICAL') {
			return <div className='text-red-600'>???? Critical</div>;
		}
	}

	return (
		<Paper sx={{width: '100%', overflow: 'hidden'}}>
			<div className='text-bg text-white p-2 bg-main-100 font-bold text-lg'>Worldwide Vaccine Shipment</div>
			<TableContainer sx={{maxHeight: 260}}>
				<Table stickyHeader aria-label='sticky table'>
					<TableHead>
						{!loading && (
							<TableRow>
								{tableHeader.map((column) => (
									<TableCell
										key={column.id}
										align={column.align}
										style={{minWidth: column.minWidth}}
										className='font-bold text-white bg-main-100 border-t border-white'
									>
										{column.label}
									</TableCell>
								))}
							</TableRow>
						)}
					</TableHead>

					<TableBody>
						{activeDeliveriesData.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => {
							return (
								<>
									<TableRow key={row.deliveryId}>
										<TableCell align='left'>{row.startHarbor.countryName}</TableCell>
										<TableCell align='left'>{row.destinationHarbor.countryName}</TableCell>
										<TableCell align='left'>{row.vaccineType}</TableCell>
										<TableCell align='left'>{row.quantity}</TableCell>
										<TableCell align='left'>{formatDate(row.createdAt)}</TableCell>
										<TableCell align='left'>{formatDate(row.estimatedArrivalDate)}</TableCell>
										<TableCell align='left'>{formateStatus(row.deliveryStatus)}</TableCell>
										<TableCell align='left'>{formateUrgency(row.urgency)}</TableCell>
									</TableRow>
								</>
							);
						})}
					</TableBody>
				</Table>
			</TableContainer>
			<TablePagination
				rowsPerPageOptions={[5, 10, 20]}
				component='div'
				count={activeDeliveriesData.length}
				rowsPerPage={rowsPerPage}
				page={page}
				onPageChange={handleChangePage}
				onRowsPerPageChange={handleChangeRowsPerPage}
			/>
		</Paper>
	);
}
